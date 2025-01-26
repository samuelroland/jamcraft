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
        Sample savedSample = addTestSample("Sample test");

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
    public void testFindByName() {
        addTestSample("Sample test");
        var samples = sampleRepository.searchByName("Sample test");
        assertEquals(samples.getFirst().getName(), "Sample test");
    }

    @Test
    public void testDeleteById() {
        Sample savedSample = addTestSample("Sample test");
        Integer sampleId = savedSample.getId();
        sampleRepository.deleteById(sampleId);
        assertFalse(sampleRepository.findById(sampleId).isPresent(), "Sample should be deleted.");
    }

    private Sample addTestSample(String name){
        Sample sample = new Sample();
        sample.setName(name);
        sample.setDuration(1.0);
        sample.setFilepath("/root");
        return sampleRepository.save(sample);
    }
}
