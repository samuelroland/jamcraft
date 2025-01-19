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
