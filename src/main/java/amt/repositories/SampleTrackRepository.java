package amt.repositories;

import amt.entities.SampleTrack;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class SampleTrackRepository extends BaseRepository<SampleTrack, Integer> {

    public SampleTrackRepository() {
        super(SampleTrack.class);
    }

    public Optional<SampleTrack> findByTrackIdAndSampleId(Integer trackId, Integer sampleId) {
        String query = "SELECT st FROM SampleTrack st WHERE st.track.id = :trackId AND st.sample.id = :sampleId";
        return entityManager.createQuery(query, SampleTrack.class)
                .setParameter("trackId", trackId)
                .setParameter("sampleId", sampleId)
                .getResultStream()
                .findFirst();
    }
}
