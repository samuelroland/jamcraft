import SampleItem from './SampleItem.tsx';
import { Sample } from '../../types.ts';
import { useEffect, useRef, useState } from 'react';
import WaveSurfer from 'wavesurfer.js';
import UploadDropZone from './UploadDropZone.tsx';
import { toast } from 'react-toastify';
import { LIBRARY_COLORS as colors } from '../constants.ts';

function Library() {
    // Samples
    const [samples, setSamples] = useState<Sample[]>([]);
    const [currentIndex, setCurrentIndex] = useState<number | null>(null);

    // WaveSurfer
    const waveSurferRef = useRef<WaveSurfer | null>(null);
    const containerRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        // Call API to retrieve samples
        fetch('/samples')
            .then((response) => response.json())
            .then((data) => setSamples(data))
            .catch((error) => {
                toast.error('Cannot fetch library samples');
                console.error('Error while fetching samples: ', error);
            });
    }, []);

    useEffect(() => {
        if (currentIndex === null || !containerRef.current) return;

        // Destroy existing WaveSurfer instance
        if (waveSurferRef.current) waveSurferRef.current.destroy();

        // Initialize WaveSurfer for the current sample
        waveSurferRef.current = WaveSurfer.create({
            container: containerRef.current,
            waveColor: colors[currentIndex % colors.length],
            progressColor: 'rgb(100, 0, 100)',
            url: '/audio/' + samples[currentIndex].filepath,
        });

        // Clean up WaveSurfer on unmount
        return () => {
            waveSurferRef.current?.destroy();
            waveSurferRef.current = null;
        };
    }, [currentIndex, samples]);

    const togglePlay = (index: number) => {
        if (currentIndex === index) waveSurferRef.current?.playPause();
        else setCurrentIndex(index); // Update current sample index
    };

    if (samples.length === 0) return <div>Loading samples...</div>;

    return (
        <div className="bg-white-100 rounded-md shadow-md ">
            <h1 className="text-2xl font-bold mb-2">Library</h1>
            <UploadDropZone onSuccessfulUpload={(sample: Sample) => setSamples([...samples, sample])}></UploadDropZone>
            <div className="z-10 py-2 grid grid-cols-1 gap-2 ">
                {samples.map((sample, index) => (
                    <SampleItem
                        key={sample.id}
                        sample={sample}
                        color={colors[index % colors.length]}
                        togglePlayCallback={() => togglePlay(index)}
                        containerRef={currentIndex === index ? containerRef : null}
                    />
                ))}
            </div>
        </div>
    );
}

export default Library;
