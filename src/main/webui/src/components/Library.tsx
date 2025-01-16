import SampleItem from './SampleItem.tsx';
import { Sample } from "../../types.ts";
import { useEffect, useState } from "react";

function Library() {
    const [samples, setSamples] = useState<Sample[]>([]);
    const [loading, setLoading] = useState<boolean>(true);

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

    if (loading)
        return <div>Loading samples...</div>;

    return (
        <div className="p-4 bg-white-100 rounded-md shadow-md">
            <h1 className="text-2xl font-bold mb-4">Library</h1>
            <div className="grid grid-cols-1 gap-4">
                {samples.map((sample, index) => {
                    // Assign a color to each sample, modulo the number of colors
                    const color = colors[index % colors.length];
                    return <SampleItem key={sample.id} sample={sample} color={color}/>;
                })}
            </div>
        </div>
    );
}

export default Library;
