package amt.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TrackDTO(
        Integer id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        List<SampleInTrackDTO> samples) {
}
