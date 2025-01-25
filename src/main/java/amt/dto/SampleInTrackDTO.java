package amt.dto;

/**
 * Data Transfer Object (DTO) for representing a {@code Sample} within a track.
 * Provides information about the sample, its associated track, and its position in the track.
 *
 * @param id        The unique identifier of the sample-track association.
 * @param sample    The {@link SampleDTO} representing the sample.
 * @param trackId   The identifier of the associated track.
 * @param trackName The name of the associated track.
 * @param startTime The start time of the sample within the track.
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
public record SampleInTrackDTO(
        Integer id,
        SampleDTO sample,
        Integer trackId, // Do not put a TrackDTO => circular reference
        String trackName,
        Double startTime) {
}
