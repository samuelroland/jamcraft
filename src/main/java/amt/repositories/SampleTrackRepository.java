package amt.repositories;

import amt.entities.SampleTrack;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SampleTrackRepository extends BaseRepository<SampleTrack, Long> {

    public SampleTrackRepository() {
        super(SampleTrack.class);
    }
}
