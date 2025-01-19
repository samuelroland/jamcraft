package amt.dto;

import java.time.LocalDateTime;

public record UserDTO(
        Integer id,
        String name,
        LocalDateTime createdAt) {
}
