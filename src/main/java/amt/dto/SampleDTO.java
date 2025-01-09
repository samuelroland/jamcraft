package amt.dto;

import java.time.LocalDateTime;

public record SampleDTO(
        Long id,
        String name,
        String filepath,
        Double duration,
        LocalDateTime createdAt
) {}
