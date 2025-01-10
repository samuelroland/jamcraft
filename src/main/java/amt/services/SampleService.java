package amt.services;

import amt.dto.DtoConverter;
import amt.dto.SampleDTO;
import amt.entities.Sample;
import amt.repositories.SampleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class SampleService implements DtoConverter<Sample, SampleDTO> {

    @Inject
    SampleRepository sampleRepository;

    @Override
    public Sample fromDTO(SampleDTO dto) {
        Sample sample = new Sample();
        sample.setId(dto.id());
        sample.setName(dto.name());
        sample.setFilepath(dto.filepath());
        sample.setDuration(dto.duration());
        return sample;
    }

    @Override
    public SampleDTO toDTO(Sample sample) {
        return new SampleDTO(
                sample.getId(),
                sample.getName(),
                sample.getFilepath(),
                sample.getDuration(),
                sample.getCreatedAt()
        );
    }

    public List<SampleDTO> getAllSamples() {
        return sampleRepository.findAll().stream().map(this::toDTO).toList();
    }

    public SampleDTO getSampleById(Long id) {
        return sampleRepository.findById(id).map(this::toDTO).orElseThrow(() ->
                new IllegalArgumentException("Sample not found"));
    }

    @Transactional
    public void saveSample(SampleDTO sampleDTO) {
        sampleRepository.save(fromDTO(sampleDTO));
    }

    @Transactional
    public void deleteSample(Long id) {
        sampleRepository.deleteById(id);
    }

    public List<SampleDTO> searchSamplesByName(String name) {
        return sampleRepository.searchByName(name).stream().map(this::toDTO).toList();
    }
}
