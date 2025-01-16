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

@ApplicationScoped
public class SampleTrackService implements DtoConverter<SampleTrack, SampleInTrackDTO> {

    @Inject
    TrackRepository trackRepository;
    @Inject
    SampleRepository sampleRepository;
    @Inject
    SampleTrackRepository sampleTrackRepository;

    @Override
    public SampleTrack fromDTO(SampleInTrackDTO dto) {
        return null;
    }

    @Override
    public SampleInTrackDTO toDTO(SampleTrack entity) {
        return null;
    }

    @Transactional
    public void addSampleToTrack(Long trackId, Long sampleId, Double startTime) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new IllegalArgumentException("Track not found"));
        Sample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> new IllegalArgumentException("Sample not found"));

        SampleTrack sampleTrack = new SampleTrack();
        sampleTrack.setTrack(track);
        sampleTrack.setSample(sample);
        sampleTrack.setStartTime(startTime);

        sampleTrackRepository.save(sampleTrack);
    }

    @Transactional
    public void removeSampleFromTrack(Long trackId, Long sampleId) {
        SampleTrack sampleTrack = sampleTrackRepository.findByTrackIdAndSampleId(trackId, sampleId)
                .orElseThrow(() -> new IllegalArgumentException("SampleTrack not found"));
        sampleTrackRepository.deleteById(sampleTrack.getId());
    }

    @Transactional
    public void updateSampleTrackPosition(Long instanceId, Double newStartTime) {
        SampleTrack sampleTrack = sampleTrackRepository.findById(instanceId)
                .orElseThrow(() -> new IllegalArgumentException("SampleTrack not found with ID: " + instanceId));

        sampleTrack.setStartTime(newStartTime);
        sampleTrackRepository.save(sampleTrack);
    }

}
