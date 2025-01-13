package amt.dto;

import java.time.LocalDateTime;

public record SampleInTrackDTO (
        Long id,
        String name,
        String filepath,
        Double duration,
        LocalDateTime createdAt,
        Double startTime
) {}

// TODO: add instanceId as sample_tracks.id
