package amt.dto;

public record SampleInTrackDTO(
        Integer id,
        SampleDTO sample,
        Integer trackId, // Do not put a TrackDTO => circular reference
        String trackName,
        Double startTime) {
}
