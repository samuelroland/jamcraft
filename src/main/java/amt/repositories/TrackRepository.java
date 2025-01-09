package amt.repositories;

import amt.entities.Track;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TrackRepository extends BaseRepository<Track, Long> {

    public TrackRepository() {
        super(Track.class);
    }
}
