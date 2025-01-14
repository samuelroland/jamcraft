package amt.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TrackDTO(
        Long id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        List<SampleInTrackDTO> samples
) {}
