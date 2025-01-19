import TrackItem from './TrackItem.tsx';
import { SampleInTrack, Track } from './../../types.ts';
import React, { useEffect, useMemo, useState } from 'react';
import { EditServiceClient } from '../grpc/edit.client.ts';
import { GrpcWebFetchTransport } from '@protobuf-ts/grpcweb-transport';
import { PROXY_BASE_URL } from '../constants.ts';

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
            })
            .catch((error) => {
                console.error('Error while fetching tracks: ', error);
            });
    }, []);

    const handleDrop = (e: React.DragEvent, trackId: number) => {
        e.preventDefault();

        const sampleData = JSON.parse(e.dataTransfer.getData('text/plain'));
        const newSample: SampleInTrack = JSON.parse(sampleData);

        setTracks((prevTracks) =>
            prevTracks.map((track) => {
                if (track.id === trackId) {
                    return {
                        ...track,
                        samples: [...track.samples, newSample], // Add sample to track
                        modifiedAt: new Date(), // Update modification time
                    };
                }

                return track;
            }),
        );
    };

    const handleDragOver = (e: React.DragEvent) => {
        e.preventDefault(); // Required for allowing drop
    };

    // @ts-ignore
    // Calculer la durée maximale parmi toutes les tracks
    const maxDuration = tracks.reduce((max, track) => {
        const trackDuration = track.samples.reduce((maxSample, sample) => {
            return Math.max(maxSample, (sample.startTime || 0) + (sample.duration || 0));
        }, 0);
        return Math.max(max, trackDuration);
    }, 0);

    // @ts-ignore
    const formatTime = (time: number) => {
        const minutes = Math.floor(time / 60);
        const seconds = Math.floor(time % 60);
        return `${minutes}:${seconds.toString().padStart(2, '0')}`;
    };

    return (
        <div className="track-list grid grid-cols-1 w-full">
            {tracks.map((track) => (
                <div key={track.id} className="track rounded" onDragOver={handleDragOver} onDrop={(e) => handleDrop(e, track.id)}>
                    <TrackItem id={track.id} name={track.name} samples={track.samples} createdAt={track.createdAt} modifiedAt={track.modifiedAt} />
                </div>
            ))}
        </div>
    );
}

export default TrackList;
