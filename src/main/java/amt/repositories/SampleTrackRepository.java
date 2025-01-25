package amt.repositories;

import amt.entities.SampleTrack;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for managing {@link SampleTrack} entities.
 * Extends the {@link BaseRepository} to inherit basic CRUD operations.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@ApplicationScoped
public class SampleTrackRepository extends BaseRepository<SampleTrack, Integer> {

    public SampleTrackRepository() {
        super(SampleTrack.class);
    }
}
