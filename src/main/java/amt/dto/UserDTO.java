package amt.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for representing a {@code User}.
 * Encapsulates basic information about a user, including their ID, name, and creation timestamp.
 *
 * @param id        The unique identifier of the user.
 * @param name      The name of the user.
 * @param createdAt The timestamp when the user was created.
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 * @implNote Natively Records implements {@link  Serializable}, but for whatever reason without explicit declaration
 * It wouldn't work with JMS...
 */
public record UserDTO(Integer id, String name, LocalDateTime createdAt) implements Serializable {
}
