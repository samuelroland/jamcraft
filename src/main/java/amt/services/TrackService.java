package amt.services;

import amt.dto.DtoConverter;
import amt.dto.SampleInTrackDTO;
import amt.dto.TrackDTO;
import amt.entities.Sample;
import amt.entities.SampleTrack;
import amt.entities.Track;
import amt.repositories.SampleRepository;
import amt.repositories.SampleTrackRepository;
import amt.repositories.TrackRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class TrackService implements DtoConverter<Track, TrackDTO> {

    @Inject
    TrackRepository trackRepository;

    @Inject
    SampleRepository sampleRepository;

    @Inject
    SampleTrackRepository sampleTrackRepository;

    @Override
    public Track fromDTO(TrackDTO trackDTO) {
        Track track = new Track();
        track.setId(trackDTO.id());
        track.setName(trackDTO.name());

        // Convert samples in the DTO to SampleTrack entities
        List<SampleTrack> sampleTracks = trackDTO.samples().stream().map(sampleInTrackDTO -> {
            Sample sample = sampleRepository.findById(sampleInTrackDTO.id())
                    .orElseThrow(() -> new IllegalArgumentException("Sample not found for ID: " + sampleInTrackDTO.id()));

            SampleTrack sampleTrack = new SampleTrack();
            sampleTrack.setTrack(track); // Associate with this track
            sampleTrack.setSample(sample);
            sampleTrack.setStartTime(sampleInTrackDTO.startTime());
            return sampleTrack;
        }).toList();

        track.setSamples(sampleTracks);

        return track;
    }

    @Override
    public TrackDTO toDTO(Track track) {
        List<SampleInTrackDTO> sampleInTrackDTOs = track.getSamples().stream().map(sampleTrack -> {
            Sample sample = sampleTrack.getSample();
            return new SampleInTrackDTO(
                    sample.getId(),
                    sample.getName(),
                    sample.getFilepath(),
                    sample.getDuration(),
                    sample.getCreatedAt(),
                    sampleTrack.getStartTime()
            );
        }).toList();

        return new TrackDTO(
                track.getId(),
                track.getName(),
                track.getCreatedAt(),
                track.getModifiedAt(),
                sampleInTrackDTOs
        );
    }

    public List<TrackDTO> getAllTracks() {
        return trackRepository.findAll().stream().map(this::toDTO).toList();
    }

    public TrackDTO getTrackById(Long id) {
        return trackRepository.findById(id).map(this::toDTO).orElseThrow(() ->
                new IllegalArgumentException("Track not found"));
    }

    @Transactional
    public void saveTrack(TrackDTO trackDTO) {
        trackRepository.save(fromDTO(trackDTO));
    }

    @Transactional
    public void deleteTrack(Long id) {
        trackRepository.deleteById(id);
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
}

