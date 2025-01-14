import Track from "./Track"

function TrackList(){
    const tracks = [
        {
            "id" : 0,
            "name" : "Track 1"
        },
        {
            "id" : 1,
            "name" : "Track 2"
        },
        {
            "id" : 2,
            "name" : "Track 3"
        }
    ]
    const trackListItems: JSX.Element[] = tracks.map((track) => {
        return <Track duration={30} id={track.id} name={track.name}></Track>
    })

    return (
        <div>
            {trackListItems}
        </div>
    )
}

export default TrackList

// !!! WIP !!!

// import { useState } from 'react';
// import React from 'react';
//
// function TrackList() {
//     const [tracks, setTracks] = useState<Record<number, string[]>>({
//         1: [],
//         2: [],
//         3: [],
//     });
//
//     const handleDrop = (e: React.DragEvent, trackId: number) => {
//         e.preventDefault();
//         const sample = e.dataTransfer.getData('text/plain');
//         setTracks((prevTracks) => ({
//             ...prevTracks,
//             [trackId]: [...prevTracks[trackId], sample],
//         }));
//         console.log(`Added sample "${sample}" to Track ${trackId}`);
//     };
//
//     const handleDragOver = (e: React.DragEvent) => {
//         e.preventDefault(); // Permet le drop
//     };
//
//     return (
//         <div className="track-list">
//             <h1>Tracks</h1>
//             {Object.entries(tracks).map(([trackId, samples]) => (
//                 <div
//                     key={trackId}
//                     className="track"
//                     onDragOver={handleDragOver}
//                     onDrop={(e) => handleDrop(e, parseInt(trackId))}
//                 >
//                     <h2>Track {trackId}</h2>
//                     <div className="samples">
//                         {samples.map((sample, index) => (
//                             <div key={index} className="track-sample">
//                                 {sample}
//                             </div>
//                         ))}
//                     </div>
//                 </div>
//             ))}
//         </div>
//     );
// }
//
// export default TrackList;
