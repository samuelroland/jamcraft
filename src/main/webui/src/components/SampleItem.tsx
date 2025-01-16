import React, { useRef, useState } from 'react';
import { Sample } from "../../types.ts";

interface SampleItemColor {
    sample: Sample;
    color: string;
}

function SampleItem({ sample, color }: SampleItemColor) {
    const [isPlaying, setIsPlaying] = useState<boolean>(false);
    const [currentTime, setCurrentTime] = useState<number>(0);

    const audioRef = useRef<HTMLAudioElement | null>(null);
    const requestRef = useRef<number>();

    // Drag and drop
    const handleDragStart = (e: React.DragEvent<HTMLDivElement>) => {
        const sampleData = JSON.stringify(sample);
        e.dataTransfer.setData('text/plain', sampleData);
    }

    // Play
    const handlePlay = () => {
        // Create audio element if not exists
        if (!audioRef.current) {
            audioRef.current = new Audio(sample.filepath)
            audioRef.current.onended = handleStop; // Stop when finished
        }

        // Play from current time
        audioRef.current.currentTime = currentTime
        audioRef.current.play().then(() => setIsPlaying(true))

        // Update current time
        const updateCurrentTime = () => {
            if (audioRef.current) {
                setCurrentTime(audioRef.current.currentTime)

                // Stop if finished
                if (audioRef.current.currentTime >= sample.duration)
                    handleStop()
            }
            // Request next frame
            requestRef.current = requestAnimationFrame(updateCurrentTime)
        }

        // Start updating current time
        requestRef.current = requestAnimationFrame(updateCurrentTime)
    }

    // Pause
    const handlePause = () => {
        if (audioRef.current) {
            audioRef.current.pause()
            setIsPlaying(false)

            // Stop updating animation frame
            if (requestRef.current)
                cancelAnimationFrame(requestRef.current)
        }
    }

    // Stop
    const handleStop = () => {
        // Stop audio and reset time
        if (audioRef.current) {
            audioRef.current.pause()
            audioRef.current.currentTime = 0

            setIsPlaying(false)
            setCurrentTime(0)

            // Stop updating animation frame
            if (requestRef.current)
                cancelAnimationFrame(requestRef.current)
        }
    }

    return (
        <div
            draggable
            onDragStart={handleDragStart}
            className="p-4 border rounded-md shadow-sm transition cursor-move"
            style={{ backgroundColor: color }}
            onClick={isPlaying ? handlePause : handlePlay}
        >
            <p className="text-sm text-white font-medium">{sample?.name}</p>
            <p className="text-xs text-white">{sample?.duration.toFixed(1)}s</p>
        </div>
    );
}

export default SampleItem;
