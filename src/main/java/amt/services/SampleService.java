package amt.services;

import amt.entities.Sample;
import amt.repositories.SampleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class SampleService {

    @Inject
    SampleRepository sampleRepository;

    public List<Sample> getAllSamples() {
        return sampleRepository.findAll();
    }

    public Sample getSampleById(Long id) {
        return sampleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sample not found"));
    }

    public void saveSample(Sample sample) {
        sampleRepository.save(sample);
    }

    public void deleteSample(Long id) {
        sampleRepository.deleteById(id);
    }

    public List<Sample> searchSamplesByName(String name) {
        return sampleRepository.searchByName(name);
    }
}
