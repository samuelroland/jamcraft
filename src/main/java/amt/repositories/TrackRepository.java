package amt.repositories;

import java.util.List;

import amt.entities.Track;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class TrackRepository extends BaseRepository<Track, Long> {

    @PersistenceContext
    protected EntityManager entityManager;

    public TrackRepository() {
        super(Track.class);
    }

    public List<Track> findAllWithSamplesLoaded() {
        String query = "SELECT e FROM Track e left join fetch e.samples";
        return entityManager.createQuery(query, Track.class).getResultList();
    }
}
