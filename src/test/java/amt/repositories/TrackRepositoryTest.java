package amt.repositories;

import amt.entities.Track;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class TrackRepositoryTest {
    @Inject
    TrackRepository trackRepository;

    @Test
    public void testSaveAndFindById() {
        Track track = new Track();
        track.setName("Guitar");

        Track savedTrack = trackRepository.save(track);

        assertNotNull(savedTrack.getId(), "Saved track's ID should not be null.");
        assertEquals("Guitar", savedTrack.getName(), "Track's name should match.");

        Track retrievedTrack = trackRepository.findById(savedTrack.getId()).orElse(null);
        assertNotNull(retrievedTrack, "Track should be retrievable by ID.");
        assertEquals(savedTrack.getId(), retrievedTrack.getId(), "IDs should match.");
    }

    @Test
    public void testFindAll() {
        List<Track> tracks = trackRepository.findAll();
        assertNotNull(tracks, "Track list should not be null.");
    }

    @Test
    public void testDeleteById() {
        Track track = new Track();
        track.setName("Drums");

        Track savedTrack = trackRepository.save(track);
        Integer trackId = savedTrack.getId();

        trackRepository.deleteById(trackId);

        assertFalse(trackRepository.findById(trackId).isPresent(), "Track should be deleted.");
    }
}
