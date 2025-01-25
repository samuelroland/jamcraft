package amt.services;

import amt.dto.DtoConverter;
import amt.dto.SampleInTrackDTO;
import amt.dto.TrackDTO;
import amt.entities.SampleTrack;
import amt.repositories.SampleTrackRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service for managing {@link SampleTrack} entities, including operations for creating,
 * updating, and removing sample tracks. Implements {@link DtoConverter} for converting
 * between entities and DTOs.
 * The service ensures that database operations are wrapped in transactions where required.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@ApplicationScoped
public class SampleTrackService implements DtoConverter<SampleTrack, SampleInTrackDTO> {

    @Inject
    SampleTrackRepository sampleTrackRepository;

    @Inject
    SampleService sampleService;

    @Inject
    TrackService trackService;

    /**
     * Converts a {@link SampleInTrackDTO} into a {@link SampleTrack} entity.
     *
     * @param dto the DTO to convert
     * @return the corresponding {@link SampleTrack} entity
     * @throws IllegalArgumentException if the sample or track ID in the DTO is null
     */
    @Override
    public SampleTrack fromDTO(SampleInTrackDTO dto) {
        if (dto.sample() == null || dto.trackId() == null) {
            throw new IllegalArgumentException("Sample or Track ID cannot be null.");
        }
        SampleTrack sampleTrack = new SampleTrack();
        sampleTrack.setId(dto.id());
        sampleTrack.setStartTime(dto.startTime());
        sampleTrack.setSample(sampleService.fromDTO(dto.sample()));
        sampleTrack.setTrack(trackService.fromDTO(new TrackDTO(dto.trackId(), dto.trackName(), null, null, null)));
        return sampleTrack;
    }

    /**
     * Converts a {@link SampleTrack} entity into a {@link SampleInTrackDTO}.
     *
     * @param entity the entity to convert
     * @return the corresponding {@link SampleInTrackDTO}
     * @throws IllegalArgumentException if the {@link SampleTrack} entity is null
     */
    @Override
    public SampleInTrackDTO toDTO(SampleTrack entity) {
        if (entity == null) {
            throw new IllegalArgumentException("SampleTrack entity cannot be null");
        }
        return new SampleInTrackDTO(
                entity.getId(),
                sampleService.toDTO(entity.getSample()),
                entity.getTrack().getId(),
                entity.getTrack().getName(),
                entity.getStartTime());
    }

    /**
     * Removes a {@link SampleTrack} entity by its ID.
     *
     * @param instanceId the ID of the sample track to remove
     * @return the removed {@link SampleInTrackDTO}
     * @throws IllegalArgumentException if no {@link SampleTrack} is found with the given ID
     */
    @Transactional
    public SampleInTrackDTO removeSampleTrack(Integer instanceId) {
        var removed = sampleTrackRepository.findById(instanceId)
                .orElseThrow(() -> new IllegalArgumentException("SampleTrack not found with ID: " + instanceId));

        sampleTrackRepository.deleteById(instanceId);
        System.out.println(
                "Sample " + removed.getSample().getName()
                + " with instance ID " + removed.getId()
                + " has been removed from track " + removed.getTrack().getName());

        return toDTO(removed);
    }

    /**
     * Creates a new {@link SampleTrack} entity.
     *
     * @param sampleId     the ID of the sample to associate with the track
     * @param trackId      the ID of the track
     * @param newStartTime the starting time of the sample on the track
     * @return the created {@link SampleInTrackDTO}
     * @throws IllegalArgumentException if the sample or track with the given IDs is not found
     */
    @Transactional
    public SampleInTrackDTO createSampleTrack(Integer sampleId, Integer trackId, Double newStartTime) {
        var track = trackService.getTrackById(trackId);
        var sample = sampleService.getSampleById(sampleId);
        SampleTrack sampleTrack = new SampleTrack();
        sampleTrack.setTrack(trackService.fromDTO(track));
        sampleTrack.setSample(sampleService.fromDTO(sample));
        sampleTrack.setStartTime(newStartTime);
        var st = sampleTrackRepository.save(sampleTrack);
        System.out.println("SampleTrack " + st.getId() + " with track id " + st.getTrack().getId() + " has been added.");
        return toDTO(st);
    }

    /**
     * Updates the start time of an existing {@link SampleTrack}.
     *
     * @param instanceId   the ID of the sample track to update
     * @param newStartTime the new starting time of the sample on the track
     * @throws IllegalArgumentException if the {@link SampleTrack} with the given ID is not found
     */
    @Transactional
    public void updateSampleTrackPosition(Integer instanceId, Double newStartTime) {
        SampleTrack sampleTrack = sampleTrackRepository.findById(instanceId)
                .orElseThrow(() -> new IllegalArgumentException("SampleTrack not found with ID: " + instanceId));
        Double oldStartTime = sampleTrack.getStartTime();
        sampleTrack.setStartTime(newStartTime);
        sampleTrackRepository.save(sampleTrack);
        System.out.println("Sample '" + sampleTrack.getSample().getName()
                           + "' on track '" + sampleTrack.getTrack().getName()
                           + "' starting at " + oldStartTime
                           + " has been moved to " + newStartTime);
    }
}
