package amt.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record UserDTO(
        Integer id,
        String name,
        LocalDateTime createdAt)implements Serializable {
}
