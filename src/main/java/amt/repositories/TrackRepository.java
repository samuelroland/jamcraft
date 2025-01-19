package amt.repositories;

import java.util.List;
import amt.entities.Track;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TrackRepository extends BaseRepository<Track, Integer> {

    public TrackRepository() {
        super(Track.class);
    }

    public List<Track> findAllWithSamplesLoaded() {
        String query = "SELECT e FROM Track e left join fetch e.samples";
        return entityManager.createQuery(query, Track.class).getResultList();
    }
}
