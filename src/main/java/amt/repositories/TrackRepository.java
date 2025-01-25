package amt.repositories;

import java.util.List;
import amt.entities.Track;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for managing {@link Track} entities.
 * Extends the {@link BaseRepository} to inherit basic CRUD operations.
 * Provides additional methods specific to the {@link Track} entity.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@ApplicationScoped
public class TrackRepository extends BaseRepository<Track, Integer> {

    public TrackRepository() {
        super(Track.class);
    }

    /**
     * Retrieves all tracks with their associated samples loaded.
     * Uses a join fetch to minimize the number of queries.
     *
     * @return A list of tracks with their samples eagerly loaded.
     */
    public List<Track> findAllWithSamplesLoaded() {
        String query = "SELECT e FROM Track e left join fetch e.samples";
        return entityManager.createQuery(query, Track.class).getResultList();
    }
}
