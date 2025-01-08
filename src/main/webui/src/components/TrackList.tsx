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