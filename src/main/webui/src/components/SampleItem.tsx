import React from 'react';
import { Sample } from "../../types.ts";

type SampleProps = {
    sample: Sample;
    color: string;
};

function SampleItem({ sample, color }: SampleProps) {
    const handleDragStart = (e: React.DragEvent<HTMLDivElement>) => {
        e.dataTransfer.setData('sample', JSON.stringify(sample));
    };

    return (
        <div
            draggable
            onDragStart={handleDragStart}
            className="p-4 border rounded-md shadow-sm transition cursor-move"
            style={{ backgroundColor: color }}
        >
            <p className="text-sm text-white font-medium">{sample.name}</p>
            <p className="text-xs text-white">{sample.duration}s</p>
        </div>
    );
}

export default SampleItem;
