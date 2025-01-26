package amt.services;

import amt.dto.SampleInTrackDTO;
import amt.dto.TrackDTO;
import amt.entities.Sample;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class TrackServiceTest {

    @Inject
    TrackService trackService;

    @Inject
    SampleService sampleService;

    @Test
    public void testSaveAndGetTrack() {
        // Create and save a track
        TrackDTO newTrack = new TrackDTO(null, "New Track", null, null, Collections.emptyList());
        TrackDTO savedTrack = trackService.saveTrack(newTrack);

        // Retrieve the track by ID
        TrackDTO retrievedTrack = trackService.getTrackById(savedTrack.id());

        // Verify results
        assertNotNull(retrievedTrack, "Retrieved track should not be null");
        assertEquals("New Track", retrievedTrack.name(), "Track name should match");
    }

    @Test
    public void testGetAllTracksWithSamples() {
        // Create and save a track with samples
        Sample sample = new Sample();
        sample.setName("Sample 1");
        sample.setFilepath("path/to/sample1.mp3");
        sample.setDuration(10.0);
        var savedSample = sampleService.saveSample(sampleService.toDTO(sample));

        SampleInTrackDTO sampleInTrackDTO = new SampleInTrackDTO(savedSample.id(), savedSample, null, null, 5.0);
        TrackDTO trackDTO = new TrackDTO(null, "Track with Samples", null, null, List.of(sampleInTrackDTO));

        trackService.saveTrack(trackDTO);

        // Retrieve all tracks
        List<TrackDTO> tracks = trackService.getAllTracksWithSamples();

        // Verify results
        assertNotEquals(0, tracks.size(), "There should be at least one track");
        assertEquals("Track with Samples", tracks.getLast().name(), "Track name should match");
        assertEquals(1, tracks.getLast().samples().size(), "Track should have one sample");
    }

    @Test
    public void testUpdateTrackName() {
        // Create and save a track
        TrackDTO newTrack = new TrackDTO(null, "Old Name", null, null, Collections.emptyList());
        TrackDTO savedTrack = trackService.saveTrack(newTrack);

        // Update the track name
        trackService.updateTrackName(savedTrack.id(), "New Name");

        // Retrieve the track and verify the update
        TrackDTO updatedTrack = trackService.getTrackById(savedTrack.id());
        assertEquals("New Name", updatedTrack.name(), "Track name should be updated");
    }

    @Test
    public void testTrackWithNoSamples() {
        // Create a track without samples
        TrackDTO emptyTrack = new TrackDTO(null, "Empty Track", null, null, Collections.emptyList());
        TrackDTO savedTrack = trackService.saveTrack(emptyTrack);

        // Retrieve the track and verify it has no samples
        TrackDTO retrievedTrack = trackService.getTrackById(savedTrack.id());
        assertNotNull(retrievedTrack, "Track should not be null");
        assertEquals(0, retrievedTrack.samples().size(), "Track should have no samples");
    }
}
