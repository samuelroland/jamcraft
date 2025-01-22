import { SampleInTrack, Track } from './../../types.ts';
import { useEffect, useMemo, useRef, useState } from 'react';
import { EditServiceClient } from '../grpc/edit.client.ts';
import Multitrack, { TrackOptions } from 'wavesurfer-multitrack';
import { Button } from './ui/button.tsx';
import { getGrpcTransport } from '../lib/utils.ts';
import { MIN_SAMPLE_POSITION_MSG_INTERVAL } from '../constants.ts';
import { EditAction, SampleInfo } from '../grpc/edit.ts';

function TrackList({ selfId }: { selfId: number }) {
    const [samplesInTrack, setSamplesInTrack] = useState<SampleInTrack[]>([]);
    const [projectIsLoaded, setProjectIsLoaded] = useState(false);
    const transport = getGrpcTransport();
    const editClient = useMemo(() => new EditServiceClient(transport), [transport]);

    type SampleBasicInfo = Omit<SampleInfo, 'action' | 'trackName' | 'userId'>;

    // A way to abort requests on component destroy - useful for HMR
    const controller = new AbortController();
    const { signal } = controller;

    // Buffering grpc calls to changeSamplePosition
    const [movedSamplePos, setMovedSamplePos] = useState<SampleBasicInfo>({
        instanceId: 0,
        sampleId: 0,
        startTime: 0,
        trackId: 0,
    }); // current moved sample pos
    const mspCopy = useRef(movedSamplePos); // just a copy for reactivity...
    const lastSentMovedSamplePos = useRef(movedSamplePos); // the last one sent via Grpc, so we don't send too requests too often

    // Received from another client
    const [lastPosApplied, setLastPosApplied] = useState<SampleBasicInfo>({ instanceId: 0, sampleId: 0, startTime: 0, trackId: 0 });
    const lastPosAppliedCpy = useRef(lastPosApplied);

    let multitrack: Multitrack | null = null;

    // Try to delay the start of the multitrack after first user interactions
    // to avoid the warning "An AudioContext was prevented from starting automatically. It must be created or resumed after a user gesture on the page."
    function handleLoadProject() {
        if (projectIsLoaded) return;
        fetch('/tracks')
            .then((response) => response.json())
            .then((data: Track[]) => {
                console.log(data);
                setSamplesInTrack(
                    data
                        .map((t) => t.samples)
                        .flat()
                        .sort((a, b) => a.id - b.id),
                );
            })
            .catch((error) => {
                console.error('Error while fetching tracks: ', error);
            })
            .then(() => {
                const flatSamples = samplesInTrack.map((s) => {
                    return {
                        id: s.id,
                        draggable: true,
                        startPosition: s.startTime, // start time relative to the entire multitrack
                        url: '/audio/' + s.sample.filepath,
                        volume: 1,
                        options: {
                            waveColor: 'hsl(145, 97%, 56%)',
                            progressColor: 'hsl(145, 97%, 56%)',
                        },
                        intro: {
                            endTime: 16,
                            label: s.sample.name,
                            color: 'hsl(46, 87%, 20%)',
                        },
                    } as TrackOptions;
                });
                multitrack = Multitrack.create(flatSamples, {
                    container: document.querySelector('#container')!, // required!
                    minPxPerSec: 50, // zoom level
                    rightButtonDrag: true, // set to true to drag with right mouse button
                    cursorWidth: 4,
                    cursorColor: '#D72F21',
                    trackBackground: 'rgb(6, 9, 7)',
                    trackBorderColor: '#7C7C7C',
                    dragBounds: true,
                });

                // Events
                multitrack.on('start-position-change', ({ id, startPosition }) => {
                    console.log(`Sample ${id} start position updated to ${startPosition}`);
                    const idAsInt = typeof id == 'string' ? parseInt(id) : id;
                    const sit = samplesInTrack.find((t) => t.id == idAsInt)!;
                    const startTime = Number(Number(startPosition).toFixed(6)); // significative digits

                    // TODO: fix this logic of smart ignoring
                    // This seems to work well except when a user move a sample after another user did it...
                    if (lastPosAppliedCpy.current.instanceId === id && mspCopy.current.instanceId != id) return;

                    const pos = {
                        instanceId: idAsInt,
                        startTime: startTime,
                        trackId: sit.trackId,
                        sampleId: sit.sample.id,
                    };
                    setMovedSamplePos(pos);
                    mspCopy.current = pos;
                });

                editClient.getEditEvents({ id: selfId }, { timeout: 10000000, abort: signal }).responses.onMessage((m) => {
                    if (lastSentMovedSamplePos.current.instanceId == m.instanceId) return; // try to ignore messages coming back after action

                    console.log('Got new position on sample.instanceId ' + m.instanceId + ' with startTime ' + m.startTime);
                    const idx = samplesInTrack.findIndex((s) => s.id == m.instanceId);
                    setLastPosApplied(m);
                    lastPosAppliedCpy.current = m;
                    multitrack?.setTrackStartPosition(idx, m.startTime);
                });

                multitrack.on('drop', ({ id }) => {
                    multitrack?.addTrack({
                        id,
                        url: '/examples/audio/demo.wav',
                        startPosition: 0,
                        draggable: true,
                        options: {
                            waveColor: 'hsl(25, 87%, 49%)',
                            progressColor: 'hsl(25, 87%, 20%)',
                        },
                    });
                });

                // Play/pause button
                function togglePlay() {
                    multitrack?.isPlaying() ? multitrack?.pause() : multitrack?.play();
                    const button = document.querySelector('#play') as HTMLInputElement;
                    button.textContent = multitrack?.isPlaying() ? 'Pause' : 'Play';
                }

                const button = document.querySelector('#play') as HTMLInputElement;
                button.disabled = true;
                multitrack.once('canplay', () => {
                    button.disabled = false;
                    button.onclick = togglePlay;
                });

                // Custom events for shortcuts
                document.addEventListener('keydown', (e) => {
                    function adaptTime(increment: number) {
                        const current = multitrack?.getCurrentTime() ?? 0;
                        if (current + increment < 0) multitrack?.setTime(0);
                        else multitrack?.setTime(current + increment);
                    }
                    switch (e.key) {
                        case ' ':
                            e.preventDefault();
                            togglePlay();
                            break;
                        case '0':
                            multitrack?.setTime(0);
                            break;
                        case '$':
                            multitrack?.setTime(1000000);
                            break;
                        case 'h':
                            adaptTime(-1);
                            break;
                        case 'l':
                            adaptTime(1);
                            break;
                    }
                });

                // Destroy all wavesurfer instances on unmount
                // This should be called before calling initMultiTrack again to properly clean up
                window.onbeforeunload = () => {
                    multitrack?.destroy();
                };
            });
    }

    useEffect(() => {
        // Each MIN_MOUSE_MSG, if the position has changed, send the new one
        const intervalId = setInterval(() => {
            // console.log('checking last moved sample', lastSentMovedSamplePos, mspCopy);
            if (
                lastSentMovedSamplePos.current.startTime === mspCopy.current.startTime &&
                lastSentMovedSamplePos.current.instanceId === mspCopy.current.instanceId
            )
                return;

            lastSentMovedSamplePos.current = mspCopy.current;
            console.log('sending new startTime: ', mspCopy.current.startTime);
            editClient.changeSamplePosition(
                { ...mspCopy.current, action: EditAction.UPDATE_TRACK, trackName: '', userId: selfId },
                { timeout: 1000, abort: signal },
            );
        }, MIN_SAMPLE_POSITION_MSG_INTERVAL);

        return () => {
            controller.abort();
            clearInterval(intervalId);
            multitrack?.destroy();
        };
    }, []);

    return (
        <div>
            <div className="flex space-x-4">
                <Button id="play">Play</Button>

                <Button onClick={() => handleLoadProject()}>Load project</Button>
            </div>
            <div id="container"></div>
        </div>
    );
}

export default TrackList;
