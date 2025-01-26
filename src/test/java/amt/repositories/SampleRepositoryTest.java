package amt.repositories;

import amt.entities.Sample;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class SampleRepositoryTest {

    @Inject
    SampleRepository sampleRepository;

    @Test
    public void testSaveAndFindById() {
        Sample sample = new Sample();
        sample.setName("Sample test");
        sample.setDuration(1.0);
        sample.setFilepath("/root");

        Sample savedSample = sampleRepository.save(sample);

        assertNotNull(savedSample.getId(), "Saved sample's ID should not be null.");
        assertEquals("Sample test", savedSample.getName(), "Sample's name should match.");

        Sample retrievedSample = sampleRepository.findById(savedSample.getId()).orElse(null);
        assertNotNull(retrievedSample, "Sample should be retrievable by ID.");
        assertEquals(savedSample.getId(), retrievedSample.getId(), "IDs should match.");
    }

    @Test
    public void testFindAll() {
        List<Sample> samples = sampleRepository.findAll();
        assertNotNull(samples, "Samples list should not be null.");
    }

    @Test
    public void testDeleteById() {
        Sample sample = new Sample();
        sample.setName("Second sample");
        sample.setDuration(1.0);
        sample.setFilepath("/root");

        Sample savedSample = sampleRepository.save(sample);
        Integer sampleId = savedSample.getId();

        sampleRepository.deleteById(sampleId);

        assertFalse(sampleRepository.findById(sampleId).isPresent(), "Sample should be deleted.");
    }
}
