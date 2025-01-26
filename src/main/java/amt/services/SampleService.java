package amt.services;

import amt.dto.DtoConverter;
import amt.dto.SampleDTO;
import amt.entities.Sample;
import amt.repositories.SampleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Service for managing {@link Sample} entities, including operations for creating,
 * retrieving, and converting between entities and DTOs.
 * This service interacts with the {@link SampleRepository} for database operations.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@ApplicationScoped
public class SampleService implements DtoConverter<Sample, SampleDTO> {

    @Inject
    SampleRepository sampleRepository;

    /**
     * Converts a {@link SampleDTO} to a {@link Sample} entity.
     *
     * @param dto the DTO to convert
     * @return the corresponding {@link Sample} entity
     */
    @Override
    public Sample fromDTO(SampleDTO dto) {
        Sample sample = new Sample();
        sample.setId(dto.id());
        sample.setName(dto.name());
        sample.setFilepath(dto.filepath());
        sample.setDuration(dto.duration());
        return sample;
    }

    /**
     * Converts a {@link Sample} entity to a {@link SampleDTO}.
     *
     * @param sample the entity to convert
     * @return the corresponding {@link SampleDTO}
     */
    @Override
    public SampleDTO toDTO(Sample sample) {
        return new SampleDTO(
                sample.getId(),
                sample.getName(),
                sample.getFilepath(),
                sample.getDuration(),
                sample.getCreatedAt());
    }

    /**
     * Retrieves all samples from the database.
     *
     * @return a list of all {@link SampleDTO}s
     */
    @Transactional
    public List<SampleDTO> getAllSamples() {
        return sampleRepository.findAll().stream().map(this::toDTO).toList();
    }

    /**
     * Retrieves a sample by its ID.
     *
     * @param id the ID of the sample
     * @return the corresponding {@link SampleDTO}
     * @throws IllegalArgumentException if no sample is found
     */
    @Transactional
    public SampleDTO getSampleById(Integer id) {
        return sampleRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Sample not found"));
    }

    /**
     * Saves a new sample or updates an existing one.
     *
     * @param sampleDTO the sample to save
     * @return the saved {@link SampleDTO}
     */
    @Transactional
    public SampleDTO saveSample(SampleDTO sampleDTO) {
        return toDTO(sampleRepository.save(fromDTO(sampleDTO)));
    }
}
