package amt.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object (DTO) for representing a {@code Track}.
 * Encapsulates information about a track and its associated samples.
 *
 * @param id         The unique identifier of the track.
 * @param name       The name of the track.
 * @param createdAt  The timestamp when the track was created.
 * @param modifiedAt The timestamp when the track was last modified.
 * @param samples    The list of {@link SampleInTrackDTO} representing the samples in the track.
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
public record TrackDTO(
        Integer id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        List<SampleInTrackDTO> samples) {
}
