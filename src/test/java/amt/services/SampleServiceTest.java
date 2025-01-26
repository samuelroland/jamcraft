package amt.services;

import amt.dto.SampleDTO;
import amt.entities.Sample;
import amt.repositories.SampleRepository;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class SampleServiceTest {

    @Inject
    SampleService sampleService;

    @Inject
    SampleRepository sampleRepository;

    @Test
    void testSaveSample() {
        SampleDTO sample = new SampleDTO(
                null,
                "My test sample",
                "/root",
                1.0,
                null
        );

        sampleService.saveSample(sample);
        assertEquals(sampleService.getAllSamples().getLast().name(), sample.name());
    }

    @Test
    @TestTransaction
    void testGetSample() {
        Sample sample = new Sample();
        sample.setName("Another test sample");
        sample.setDuration(42.0);
        sample.setFilepath("/somewhere");
        sampleRepository.save(sample);

        Integer id = sampleRepository.searchByName("Another test sample").getFirst().getId();
        Integer id2 = sampleService.getSampleById(id).id();
        assertEquals(id, id2, "Sample Id must match");
    }
}
