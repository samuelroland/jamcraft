package amt.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for representing a {@code Sample}.
 * Contains all necessary information to represent an audio sample.
 *
 * @param id        The unique identifier of the sample.
 * @param name      The name of the sample.
 * @param filepath  The file path where the sample is stored.
 * @param duration  The duration of the sample in seconds.
 * @param createdAt The timestamp when the sample was created.
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
public record SampleDTO(
        Integer id,
        String name,
        String filepath,
        Double duration,
        LocalDateTime createdAt) {
}
