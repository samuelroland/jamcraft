package amt.dto;

import java.time.LocalDateTime;

public record SampleInTrackDTO(
        Long id, // sample ID
        Long instanceId, // sample_track ID
        String name,
        String filepath,
        Double duration,
        LocalDateTime createdAt,
        Double startTime) {
}
