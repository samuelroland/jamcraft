package amt.dto;

import java.time.LocalDateTime;

public record SampleInTrackDTO(
        Long id,
        Long instanceId,
        String name,
        String filepath,
        Double duration,
        LocalDateTime createdAt,
        Double startTime) {
}
