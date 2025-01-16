package amt.dto;

public record SampleInTrackDTO(
        Long id,
        SampleDTO sample,
        Long trackId, // Do not put a TrackDTO => circular reference
        String trackName,
        Double startTime) {
}
