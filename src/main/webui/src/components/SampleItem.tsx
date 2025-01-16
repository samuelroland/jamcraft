import React, { useRef, useState } from 'react';
import { Sample } from "../../types.ts";

interface Props {
    sample: Sample;
    color: string;
    togglePlayCallback: () => void;
}

function SampleItem({ sample, color, togglePlayCallback }: Props) {
    // Drag and drop
    const handleDragStart = (e: React.DragEvent<HTMLDivElement>) => {
        const sampleData = JSON.stringify(sample);
        e.dataTransfer.setData('text/plain', sampleData);
    }

    return (
        <div
            draggable
            onDragStart={handleDragStart}
            className="p-4 border rounded-md shadow-sm transition cursor-move"
            style={{ backgroundColor: color }}
            onClick={togglePlayCallback}
        >
            <p className="text-sm text-white font-medium">{sample?.name}</p>
            <p className="text-xs text-white">{sample?.duration.toFixed(1)}s</p>
        </div>
    );
}

export default SampleItem;
