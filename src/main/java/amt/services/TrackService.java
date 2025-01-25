package amt.services;

import amt.dto.DtoConverter;
import amt.dto.SampleInTrackDTO;
import amt.dto.TrackDTO;
import amt.entities.Sample;
import amt.entities.SampleTrack;
import amt.entities.Track;
import amt.repositories.SampleRepository;
import amt.repositories.TrackRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Service for managing {@link Track} entities, including operations for creating,
 * updating, and retrieving tracks. Converts between {@link Track} entities and {@link TrackDTO}.
 * This service interacts with the {@link TrackRepository} and {@link SampleRepository}.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@ApplicationScoped
public class TrackService implements DtoConverter<Track, TrackDTO> {

    @Inject
    TrackRepository trackRepository;

    @Inject
    SampleRepository sampleRepository;

    @Inject
    SampleService sampleService;

    /**
     * Converts a {@link TrackDTO} to a {@link Track} entity.
     *
     * @param trackDTO the DTO to convert
     * @return the corresponding {@link Track} entity
     */
    @Override
    public Track fromDTO(TrackDTO trackDTO) {
        Track track = new Track();
        track.setId(trackDTO.id());
        track.setName(trackDTO.name());

        // Convert samples in the DTO to SampleTrack entities
        List<SampleTrack> sampleTracks = trackDTO.samples().stream().map(sampleInTrackDTO -> {
            Sample sample = sampleRepository.findById(sampleInTrackDTO.id())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Sample not found for ID: " + sampleInTrackDTO.id()));

            SampleTrack sampleTrack = new SampleTrack();
            sampleTrack.setTrack(track); // Associate with this track
            sampleTrack.setSample(sample);
            sampleTrack.setStartTime(sampleInTrackDTO.startTime());
            return sampleTrack;
        }).toList();

        track.setSamples(sampleTracks);

        return track;
    }

    /**
     * Converts a {@link Track} entity to a {@link TrackDTO}.
     *
     * @param track the entity to convert
     * @return the corresponding {@link TrackDTO}
     */
    @Override
    public TrackDTO toDTO(Track track) {
        List<SampleInTrackDTO> sampleInTrackDTOs = track.getSamples().stream().map(sampleTrack -> new SampleInTrackDTO(
                sampleTrack.getId(),
                sampleService.toDTO(sampleTrack.getSample()),
                sampleTrack.getTrack().getId(),
                sampleTrack.getTrack().getName(),
                sampleTrack.getStartTime())).toList();

        return new TrackDTO(
                track.getId(),
                track.getName(),
                track.getCreatedAt(),
                track.getModifiedAt(),
                sampleInTrackDTOs);
    }

    /**
     * Retrieves all tracks with their associated samples.
     *
     * @return a list of all {@link TrackDTO}s
     */
    public List<TrackDTO> getAllTracksWithSamples() {
        return trackRepository.findAllWithSamplesLoaded().stream().map(this::toDTO).toList();
    }

    /**
     * Retrieves a track by its ID.
     *
     * @param id the ID of the track
     * @return the corresponding {@link TrackDTO}
     * @throws IllegalArgumentException if the track is not found
     */
    public TrackDTO getTrackById(Integer id) {
        return trackRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Track " + id + " not found"));
    }

    /**
     * Updates the name of a track.
     *
     * @param trackId the ID of the track to update
     * @param newName the new name for the track
     * @throws IllegalArgumentException if the track is not found
     */
    @Transactional
    public void updateTrackName(Integer trackId, String newName) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new IllegalArgumentException("Track not found with ID: " + trackId));
        String oldName = track.getName();
        track.setName(newName);
        trackRepository.save(track);
        System.out.println("Track: " + oldName + " has been renamed " + newName);
    }

    /**
     * Saves a new track or updates an existing one.
     *
     * @param trackDTO the track to save
     * @return the saved {@link TrackDTO}
     */
    @Transactional
    public TrackDTO saveTrack(TrackDTO trackDTO) {
        var track = trackRepository.save(fromDTO(trackDTO));

        if (track.getName().isEmpty()) {
            track.setName("Track " + track.getId());
        }
        return toDTO(track);
    }
}
