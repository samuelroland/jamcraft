package amt.grpc;

import amt.*;
import amt.dto.TrackDTO;
import amt.services.SampleTrackService;
import amt.services.TrackService;
import com.google.protobuf.Empty;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import jakarta.inject.Inject;

import java.util.ArrayList;

@GrpcService
public class EditGrpcService implements EditService {

    @Inject
    TrackService trackService;
    @Inject
    SampleTrackService sampleTrackService;

    // BroadcastProcessor for hot streaming user updates
    private final BroadcastProcessor<SampleInfo> processor = BroadcastProcessor.create();

    @Override
    @RunOnVirtualThread
    // Test command: grpcurl -plaintext -d '{\"sampleId\": 3, \"instanceId\": 8,
    // \"startTime\": 42.42, \"trackId\": 4, \"userId\": 2}' localhost:9000
    // edit.EditService/ChangeSamplePosition
    public Uni<Empty> changeSamplePosition(SampleInfo request) {
        try {
            // TODO: maybe refactor this...
            // Create the sample_tracks if it doesn't exist
            if (request.getInstanceId() == 0) {
                var result = sampleTrackService.createSampleTrack(request.getSampleId(), request.getTrackId(),
                        request.getStartTime());
                System.out.println("Created new sample_tracks for " + result.id());
            } else {
                // Update the sample position in the database
                sampleTrackService.updateSampleTrackPosition(request.getInstanceId(), request.getStartTime());
                System.out.println("Updated sample position");
            }

            // // Create the response and broadcast
            processor.onNext(
                    SampleInfo.newBuilder()
                            .setAction(EditAction.UPDATE_TRACK)
                            .setUserId(request.getUserId())
                            .setInstanceId(request.getInstanceId())
                            .setSampleId(request.getSampleId())
                            .setTrackId(request.getTrackId())
                            .setTrackName(request.getTrackName())
                            .setStartTime(request.getStartTime())
                            .build());

            // Return a successful response
            return Uni.createFrom().item(Empty.getDefaultInstance());
        } catch (IllegalArgumentException e) {
            System.err.println("Error changing sample position: " + e.getMessage());
            return Uni.createFrom().failure(e);
        }
    }

    @Override
    @RunOnVirtualThread
    // Test command : grpcurl -plaintext -d '{\"instanceId\": 1, \"userId\": 2 }'
    // localhost:9000 edit.EditService/RemoveSample
    public Uni<Empty> removeSample(SampleInstanceId request) {
        try {
            // Remove the SampleTrack from the database
            var removed = sampleTrackService.removeSampleTrack(request.getInstanceId());

            // Create the response and broadcast
            processor.onNext(
                    SampleInfo.newBuilder()
                            .setAction(EditAction.UPDATE_TRACK)
                            .setUserId(request.getUserId())
                            .setInstanceId(removed.id())
                            .setSampleId(removed.sample().id())
                            .setTrackId(removed.trackId())
                            .setTrackName(removed.trackName())
                            .setStartTime(removed.startTime())
                            .build());

            return Uni.createFrom().item(Empty.getDefaultInstance());
        } catch (IllegalArgumentException e) {
            System.err.println("Error removing sample: " + e.getMessage());
            return Uni.createFrom().failure(e);
        }
    }

    // Test command: grpcurl -plaintext -d '{\"trackId\": 3, \"name\": \"Drums\",
    // \"userId\": 8 }' localhost:9000 edit.EditService/UpdateTrackName
    @Override
    @RunOnVirtualThread
    public Uni<Empty> updateTrackName(TrackInfo request) {
        try {
            trackService.updateTrackName(request.getTrackId(), request.getName());

            // Create the response and broadcast
            processor.onNext(
                    SampleInfo.newBuilder()
                            .setAction(EditAction.UPDATE_TRACK)
                            .setUserId(request.getUserId())
                            .setTrackId(request.getTrackId())
                            .setTrackName(request.getName())
                            .build());

            return Uni.createFrom().item(Empty.getDefaultInstance());
        } catch (IllegalArgumentException e) {
            System.err.println("Error renaming track id " + request.getTrackId() + " to " + request.getName() + " : "
                    + e.getMessage());
            return Uni.createFrom().failure(e);
        }
    }

    // Test command: grpcurl -plaintext -d '{\"name\": \"Test track\", \"userId\": 8
    // }' localhost:9000 edit.EditService/AddTrack
    @Override
    @RunOnVirtualThread
    public Uni<Empty> addTrack(TrackName request) {
        try {
            var track = trackService.saveTrack(new TrackDTO(null, request.getName(), null, null, new ArrayList<>()));

            // Create the response and broadcast
            processor.onNext(
                    SampleInfo.newBuilder()
                            .setAction(EditAction.CREATE_TRACK)
                            .setUserId(request.getUserId())
                            .setTrackId(track.id())
                            .setTrackName(track.name())
                            .build());

            return Uni.createFrom().item(Empty.getDefaultInstance());
        } catch (IllegalArgumentException e) {
            System.err.println("Error creating new track" + e.getMessage());
            return Uni.createFrom().failure(e);
        }
    }

    // Test command: grpcurl -plaintext -d '{\"id\": 1}' localhost:9000
    // edit.EditService/GetEditEvents
    @Override
    @RunOnVirtualThread
    public Multi<SampleInfo> getEditEvents(UserId request) {
        int id = request.getId();
        return processor.filter(user -> user.getUserId() != id);
    }
}
