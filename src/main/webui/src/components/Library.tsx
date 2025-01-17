import SampleItem from './SampleItem.tsx';
import { Sample } from '../../types.ts';
import { useEffect, useRef, useState } from 'react';
import WaveSurfer from 'wavesurfer.js';
import { toast } from 'react-toastify';

function Library() {
    // Samples
    const [samples, setSamples] = useState<Sample[]>([]);
    const [samplesColors, setSamplesColors] = useState<string[]>([]);
    const [currentIndex, setCurrentIndex] = useState<number | null>(null);

    // WaveSurfer
    const waveSurferRef = useRef<WaveSurfer | null>(null);
    const containerRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        // Call API to retrieve samples
        fetch('/samples')
            .then((response) => response.json())
            .then((data) => {
                setSamples(data); // Set samples
                // Generate colors for each sample
                const colors = ['#e83f3f', '#23852f', '#3b70be', '#e57c2f', '#5c218a'];
                setSamplesColors(data.map((_: Sample, index: number) => colors[index % colors.length]));
            })
            .catch((error) => {
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
            waveColor: samplesColors[currentIndex],
            progressColor: 'rgb(100, 0, 100)',
            url: '/audio/' + samples[currentIndex].filepath,
        });

        // Clean up WaveSurfer on unmount
        return () => {
            waveSurferRef.current?.destroy();
            waveSurferRef.current = null;
        };
    }, [currentIndex, samplesColors, samples]);

    const togglePlay = (index: number) => {
        if (currentIndex === index) waveSurferRef.current?.playPause();
        else setCurrentIndex(index); // Update current sample index
    };

    if (samples.length === 0) return <div>Loading samples...</div>;

    return (
        <div className="p-4 bg-white-100 rounded-md shadow-md">
            <h1 className="text-2xl font-bold mb-4">Library</h1>
            <div className="grid grid-cols-1 gap-4">
                {samples.map((sample, index) => (
                    <SampleItem
                        key={sample.id}
                        sample={sample}
                        color={samplesColors[index]}
                        togglePlayCallback={() => togglePlay(index)}
                        containerRef={currentIndex === index ? containerRef : null}
                    />
                ))}
            </div>
        </div>
    );
}

export default Library;
