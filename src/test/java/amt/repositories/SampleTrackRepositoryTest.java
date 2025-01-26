package amt.repositories;

import amt.entities.Sample;
import amt.entities.SampleTrack;
import amt.entities.Track;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class SampleTrackRepositoryTest {
    @Inject
    SampleTrackRepository sampleTrackRepository;

    @Inject
    TrackRepository trackRepository;

    @Inject
    SampleRepository sampleRepository;

    @Test
    public void testSaveAndFindById() {
        Sample sample = new Sample();
        sample.setName("Sample test");
        sample.setDuration(1.0);
        sample.setFilepath("/root");
        sampleRepository.save(sample);

        Track track = new Track();
        track.setName("Guitar");
        trackRepository.save(track);

        SampleTrack st = new SampleTrack();
        st.setStartTime(0.0);
        st.setSample(sample);
        st.setTrack(track);

        SampleTrack savedSt = sampleTrackRepository.save(st);

        assertNotNull(savedSt.getId(), "Saved sampleTrack's ID should not be null.");
        assertEquals("Sample test", savedSt.getSample().getName(), "SampleTrack's sample should match.");
        assertEquals("Guitar", savedSt.getTrack().getName(), "SampleTrack's track should match.");

        SampleTrack retrievedSt = sampleTrackRepository.findById(savedSt.getId()).orElse(null);
        assertNotNull(retrievedSt, "sampleTrack should be retrievable by ID.");
        assertEquals(savedSt.getId(), retrievedSt.getId(), "IDs should match.");
    }

    @Test
    public void testFindAll() {
        List<SampleTrack> st = sampleTrackRepository.findAll();
        assertNotNull(st, "SampleTracks list should not be null.");
    }

    @Test
    public void testDeleteById() {
        Sample sample = new Sample();
        sample.setName("Sample test");
        sample.setDuration(1.0);
        sample.setFilepath("/root");
        sampleRepository.save(sample);

        Track track = new Track();
        track.setName("Guitar");
        trackRepository.save(track);

        SampleTrack st = new SampleTrack();
        st.setStartTime(0.0);
        st.setSample(sample);
        st.setTrack(track);

        SampleTrack savedSt = sampleTrackRepository.save(st);
        Integer stId = savedSt.getId();

        sampleTrackRepository.deleteById(stId);

        assertFalse(sampleTrackRepository.findById(stId).isPresent(), "SampleTrack should be deleted.");
    }
}
