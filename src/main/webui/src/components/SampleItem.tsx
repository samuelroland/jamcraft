import React from 'react';
import { Sample } from '../../types.ts';

interface Props {
    sample: Sample;
    color: string;
    togglePlayCallback: () => void;
    containerRef: React.RefObject<HTMLDivElement> | null;
}

function SampleItem({ sample, color, togglePlayCallback, containerRef }: Props) {
    const handleDragStart = (e: React.DragEvent<HTMLDivElement>) => {
        const sampleData = JSON.stringify(sample);
        e.dataTransfer.setData('sample', sampleData);
    };

    const formatDuration = (duration: number) => {
        return duration < 60 ? `${duration.toFixed(0)}s` : `${Math.floor(duration / 60)}m ${(duration % 60).toFixed(0)}s`;
    };

    return (
        <div
            draggable
            onDragStart={handleDragStart}
            className="p-1 border rounded-md shadow-sm transition cursor-move"
            style={{ backgroundColor: color + 'bf' }}
            onClick={togglePlayCallback}
        >
            {containerRef && <div ref={containerRef}></div>}
            <div>
                <span className="text-sm text-white font-medium">{sample?.name}</span>
                <span className="pl-3 text-sm text-gray-50 italic">{formatDuration(sample?.duration)}</span>
            </div>
        </div>
    );
}

export default SampleItem;
