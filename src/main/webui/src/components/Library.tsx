import SampleItem from './SampleItem.tsx';
import { Sample } from "../../types.ts";
import { useEffect, useRef, useState } from "react";

function Library() {
    const [currentTime, setCurrentTime] = useState<number>(0);
    const [samples, setSamples] = useState<Sample[]>([]);
    const [isPlaying, setIsPlaying] = useState<boolean[]>([]);
    const [loading, setLoading] = useState<boolean>(true);

    const audioRef = useRef<HTMLAudioElement | null>(null);

    useEffect(() => {
        // Call API to retrieves sampels
        fetch('/samples')
            .then((response) => response.json())
            .then((data) => {
                setSamples(data); // Set samples
                setLoading(false); // End of loading
            })
            .catch((error) => {
                console.error('Error while fetching samples: ', error);
                setLoading(false);
            })
    }, []);

    // Tableau de couleurs définies
    const colors = ['#e83f3f', '#23852f', '#3b70be', '#e57c2f', '#5c218a'];

    const togglePlay = (index: number) => {
        const sample = samples[index];

        // Create audio element if not exists
        if (!audioRef.current || !isPlaying[index]) {
            if (audioRef.current)
                audioRef.current.pause()

            audioRef.current = new Audio("/audio/" + sample.filepath)
        }

        // Play from current time
        audioRef.current.currentTime = currentTime

        if (isPlaying[index]) {
            audioRef.current.pause()
            setIsPlaying({ ...isPlaying, [index]: false })
        } else {
            // Set isPlaying to true for the current sample
            const newIsPlaying = new Array(samples.length).fill(false)
            newIsPlaying[index] = true
            audioRef.current.play().then(() => setIsPlaying(newIsPlaying))
        }
    }

    if (loading)
        return <div>Loading samples...</div>;

    return (
        <div className="p-4 bg-white-100 rounded-md shadow-md">
            <h1 className="text-2xl font-bold mb-4">Library</h1>
            <div className="grid grid-cols-1 gap-4">
                {samples.map((sample, index) => {
                    // Assign a color to each sample, modulo the number of colors
                    const color = colors[index % colors.length];
                    return <SampleItem key={sample.id} sample={sample} color={color}
                                       togglePlayCallback={() => togglePlay(index)}/>;
                })}
            </div>
        </div>
    );
}

export default Library;
