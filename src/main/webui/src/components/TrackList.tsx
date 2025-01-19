import TrackItem from './TrackItem.tsx';
import { SampleInTrack, Track } from './../../types.ts';
import React, { useEffect, useMemo, useState } from 'react';
import { EditServiceClient } from '../grpc/edit.client.ts';
import { GrpcWebFetchTransport } from '@protobuf-ts/grpcweb-transport';
import { PROXY_BASE_URL } from '../constants.ts';
import Multitrack, { MultitrackTracks, TrackOptions } from 'wavesurfer-multitrack';
import { Button } from './ui/button.tsx';

function TrackList() {
    const [tracks, setTracks] = useState<Track[]>([]);

    const transport = useMemo(
        () =>
            new GrpcWebFetchTransport({
                baseUrl: PROXY_BASE_URL,
                format: 'binary',
            }),
        [],
    );
    // @ts-ignore
    const editClient = useMemo(() => new EditServiceClient(transport), [transport]);
    // editClient.changeSamplePosition({})
    // editClient.getSamplePositions({}).responses

    useEffect(() => {
        fetch('/tracks')
            .then((response) => response.json())
            .then((data) => {
                setTracks(data);
                console.log(data);
            })
            .catch((error) => {
                console.error('Error while fetching tracks: ', error);
            });

        const multitrack = Multitrack.create(
            tracks
                .map((t) => t.samples)
                .flat()
                .map((s) => {
                    return {
                        id: 1,
                        draggable: true,
                        startPosition: s.startTime, // start time relative to the entire multitrack
                        url: '/audio/' + s.sample.filepath,
                        volume: 0.95,
                        startCue: 2,
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
                }),
            {
                container: document.querySelector('#container')!, // required!
                minPxPerSec: 100, // zoom level
                rightButtonDrag: false, // set to true to drag with right mouse button
                cursorWidth: 4,
                cursorColor: '#D72F21',
                trackBackground: 'rgb(6, 9, 7)',
                trackBorderColor: '#7C7C7C',
                dragBounds: true,
            },
        );

        // Events
        multitrack.on('start-position-change', ({ id, startPosition }) => {
            console.log(`Track ${id} start position updated to ${startPosition}`);
        });

        multitrack.on('start-cue-change', ({ id, startCue }) => {
            console.log(`Track ${id} start cue updated to ${startCue}`);
        });

        multitrack.on('end-cue-change', ({ id, endCue }) => {
            console.log(`Track ${id} end cue updated to ${endCue}`);
        });

        multitrack.on('intro-end-change', ({ id, endTime }) => {
            console.log(`Track ${id} intro end updated to ${endTime}`);
        });

        multitrack.on('drop', ({ id }) => {
            multitrack.addTrack({
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
            multitrack.isPlaying() ? multitrack.pause() : multitrack.play();
            const button = document.querySelector('#play') as HTMLInputElement;
            button.textContent = multitrack.isPlaying() ? 'Pause' : 'Play';
        }

        const button = document.querySelector('#play') as HTMLInputElement;
        button.disabled = true;
        multitrack.once('canplay', () => {
            button.disabled = false;
            button.onclick = togglePlay;
        });

        // Custom events for shortcuts
        document.addEventListener('keydown', (e) => {
            switch (e.key) {
                case ' ':
                    e.preventDefault();
                    togglePlay();
                    break;
                case '0':
                    multitrack.setTime(0);
                    break;
                case '$':
                    multitrack.setTime(1000000);
                    break;
            }
        });

        // Forward/back buttons
        // const forward = document.querySelector('#forward');
        // forward.onclick = () => {
        //     multitrack.setTime(multitrack.getCurrentTime() + 30);
        // };
        // const backward = document.querySelector('#backward');
        // backward.onclick = () => {
        //     multitrack.setTime(multitrack.getCurrentTime() - 30);
        // };

        // Zoom
        // const slider = document.querySelector('input[type="range"]');
        // slider.oninput = () => {
        //     multitrack.zoom(slider.valueAsNumber);
        // };

        // Destroy all wavesurfer instances on unmount
        // This should be called before calling initMultiTrack again to properly clean up
        window.onbeforeunload = () => {
            multitrack.destroy();
        };

        // Set sinkId
        multitrack.once('canplay', async () => {
            await multitrack.setSinkId('default');
            console.log('Set sinkId to default');
        });

        return () => {
            multitrack.destroy();
        };
    }, []);

    return (
        <div>
            <div className="flex">
                <Button id="play">Play</Button>
            </div>
            <div id="container"></div>;
        </div>
    );
}

export default TrackList;
