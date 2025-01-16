package amt.services;

import amt.dto.DtoConverter;
import amt.dto.SampleInTrackDTO;
import amt.entities.Sample;
import amt.entities.SampleTrack;
import amt.entities.Track;
import amt.repositories.SampleRepository;
import amt.repositories.SampleTrackRepository;
import amt.repositories.TrackRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class SampleTrackService implements DtoConverter<SampleTrack, SampleInTrackDTO> {

    @Inject
    SampleTrackRepository sampleTrackRepository;

    @Inject
    SampleService sampleService;

    @Override
    public SampleTrack fromDTO(SampleInTrackDTO dto) {
        return null;
    }

    @Override
    public SampleInTrackDTO toDTO(SampleTrack entity) {
        return new SampleInTrackDTO(
                entity.getId(),
                sampleService.toDTO(entity.getSample()),
                entity.getTrack().getId(),
                entity.getTrack().getName(),
                entity.getStartTime());
    }

    @Transactional
    public SampleInTrackDTO removeSampleTrack(Long instanceId) {
        var removed = sampleTrackRepository.findById(instanceId)
                .orElseThrow(() -> new IllegalArgumentException("SampleTrack not found with ID: " + instanceId));

        sampleTrackRepository.deleteById(instanceId);

        return toDTO(removed);
    }

    @Transactional
    public void updateSampleTrackPosition(Long instanceId, Double newStartTime) {
        SampleTrack sampleTrack = sampleTrackRepository.findById(instanceId)
                .orElseThrow(() -> new IllegalArgumentException("SampleTrack not found with ID: " + instanceId));

        sampleTrack.setStartTime(newStartTime);
        sampleTrackRepository.save(sampleTrack);
    }

}
