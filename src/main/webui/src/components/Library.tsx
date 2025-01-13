import SampleItem from './SampleItem.tsx';

function Library() {
    const samples = [
        { id: 1, name: 'Kick Drum', filepath: 'kick.wav', duration: 0.5, createdAt: new Date() },
        { id: 2, name: 'Snare Drum', filepath: 'snare.wav', duration: 0.5, createdAt: new Date() },
        { id: 3, name: 'Hi-Hat', filepath: 'hihat.wav', duration: 1, createdAt: new Date() },
        { id: 4, name: 'Bass', filepath: 'bass.wav', duration: 2, createdAt: new Date() },
        { id: 5, name: 'Synth', filepath: 'synth.wav', duration: 1.5, createdAt: new Date() },
        { id: 6, name: 'Rock kick', filepath: 'rockKick.wav', duration: 5, createdAt: new Date() },
    ];

    // Tableau de couleurs définies
    const colors = [
        '#e83f3f',
        '#23852f',
        '#3b70be',
        '#e57c2f',
        '#5c218a',
    ];

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
