import { SampleInTrack, Track } from './../../types.ts';
import { useEffect, useMemo, useState } from 'react';
import { EditServiceClient } from '../grpc/edit.client.ts';
import { GrpcWebFetchTransport } from '@protobuf-ts/grpcweb-transport';
import { PROXY_BASE_URL } from '../constants.ts';
import Multitrack, { TrackOptions } from 'wavesurfer-multitrack';
import { Button } from './ui/button.tsx';

function TrackList() {
    const [samplesInTrack, setSamplesInTrack] = useState<SampleInTrack[]>([]);
    const [firstUserInteraction, setFirstUserInteraction] = useState(false);

    const transport = useMemo(
        () =>
            new GrpcWebFetchTransport({
                baseUrl: PROXY_BASE_URL,
                format: 'binary',
            }),
        [],
    );
    const editClient = useMemo(() => new EditServiceClient(transport), [transport]);

    // Try to delay the start of the multitrack after first user interactions
    // to avoid the warning "An AudioContext was prevented from starting automatically. It must be created or resumed after a user gesture on the page."
    useEffect(() => {
        function gotFirstInteraction() {
            setTimeout(() => {
                setFirstUserInteraction(true);
            }, 200);
            console.log('first interact');
            document.removeEventListener('click', gotFirstInteraction);
            // document.removeEventListener('mousemove', gotFirstInteraction);
        }
        if (!firstUserInteraction) {
            document.addEventListener('click', gotFirstInteraction);
            // document.addEventListener('mousemove', gotFirstInteraction);
        }
    }, []);

    useEffect(() => {
        let multitrack: Multitrack | null = null;
        if (firstUserInteraction) {
            fetch('/tracks')
                .then((response) => response.json())
                .then((data: Track[]) => {
                    console.log(data);
                    setSamplesInTrack(data.map((t) => t.samples).flat());
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

                        editClient
                            .changeSamplePosition({
                                instanceId: idAsInt,
                                startTime: startPosition,
                                trackId: sit.trackId,
                                sampleId: sit.sample.id,
                            })
                            .then((a) => {
                                console.log('req done', a);
                            });
                    });

                    editClient.getSamplePositions({}).responses.onMessage((m) => {
                        console.log('Got new position on sample.instanceId ' + m.instanceId + ' with startTime ' + m.startTime);
                        const idx = samplesInTrack.findIndex((s) => s.id == m.instanceId);
                        multitrack?.setTrackStartPosition(idx, m.startTime);
                    });

                    multitrack.on('start-cue-change', ({ id, startCue }) => {
                        console.log(`Sample ${id} start cue updated to ${startCue}`);
                    });

                    multitrack.on('end-cue-change', ({ id, endCue }) => {
                        console.log(`Sample ${id} end cue updated to ${endCue}`);
                    });

                    multitrack.on('intro-end-change', ({ id, endTime }) => {
                        console.log(`Sample ${id} intro end updated to ${endTime}`);
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

                    // Zoom
                    // const slider = document.querySelector('input[type="range"]');
                    // slider.oninput = () => {
                    //     multitrack.zoom(slider.valueAsNumber);
                    // };

                    // Destroy all wavesurfer instances on unmount
                    // This should be called before calling initMultiTrack again to properly clean up
                    window.onbeforeunload = () => {
                        multitrack?.destroy();
                    };

                    // Set sinkId
                    // multitrack.once('canplay', async () => {
                    //     await multitrack.setSinkId('default');
                    //     console.log('Set sinkId to default');
                    // });
                });
        }

        return () => {
            console.log('out of useeffect');
            multitrack?.destroy();
        };
    }, [firstUserInteraction]);

    return (
        <div>
            <div className="flex">
                <Button id="play">Play</Button>
            </div>
            <div id="container"></div>
        </div>
    );
}

export default TrackList;
