package amt.grpc;

import amt.*;
import amt.services.SampleTrackService;
import amt.services.TrackService;
import com.google.protobuf.Empty;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.MultiEmitter;
import jakarta.inject.Inject;

import java.util.concurrent.CopyOnWriteArrayList;

@GrpcService
public class EditGrpcService implements EditService {

    @Inject
    TrackService trackService; // Handles DB operations for tracks
    @Inject
    SampleTrackService sampleTrackService;

    // Active emitters for streaming sample positions
    private final CopyOnWriteArrayList<MultiEmitter<? super SampleInfo>> samplePositionEmitters = new CopyOnWriteArrayList<>();

    // Active emitters for streaming track info updates
    private final CopyOnWriteArrayList<MultiEmitter<? super TrackInfo>> trackInfoEmitters = new CopyOnWriteArrayList<>();

    // Active emitters for streaming sample uploads
    private final CopyOnWriteArrayList<MultiEmitter<? super SampleInfo>> sampleRemoveEmitters = new CopyOnWriteArrayList<>();

    // Handle incoming requests to change sample position
    // Test command: grpcurl -plaintext -d '{\"sampleId\": 3, \"instanceId\": 8, \"startTime\": 42.42, \"trackId\": 4}' localhost:9000 edit.EditService/ChangeSamplePosition
    @RunOnVirtualThread
    @Override
    public Uni<Empty> changeSamplePosition(SampleInfo request) {

        try {
            // Update the sample position in the database
            sampleTrackService.updateSampleTrackPosition(request.getInstanceId(), request.getStartTime());

            // Notify all connected clients about the update
            samplePositionEmitters.forEach(emitter -> emitter.emit(request));

            // Return a successful response
            return Uni.createFrom().item(Empty.getDefaultInstance());
        } catch (IllegalArgumentException e){
            System.err.println("Error changing sample position: " + e.getMessage());
            return Uni.createFrom().failure(e);
        }
    }

    // Handle incoming requests to remove a sample
    // Test command : grpcurl -plaintext -d '{\"instanceId\": 1}' localhost:9000 edit.EditService/RemoveSample
    @RunOnVirtualThread
    @Override
    public Uni<Empty> removeSample(SampleInstanceId request) {
        try {
            // Remove the SampleTrack from the database
            var removed = sampleTrackService.removeSampleTrack(request.getInstanceId());

            // Notify listeners with the removed SampleTrack info
            SampleInfo response = SampleInfo.newBuilder()
                    .setInstanceId(removed.id())
                    .setSampleId(removed.sample().id())
                    .setTrackId(removed.trackId())
                    .setStartTime(removed.startTime())
                    .build();

            sampleRemoveEmitters.forEach(emitter -> emitter.emit(response));
            return Uni.createFrom().item(Empty.getDefaultInstance());
        } catch (IllegalArgumentException e) {
            System.err.println("Error removing sample: " + e.getMessage());
            return Uni.createFrom().failure(e);
        }
    }

    // Test command: grpcurl -plaintext -d '{\"trackId\": 3, \"name\": \"Drums\"}' localhost:9000 edit.EditService/ChangeTrackInfo
    @RunOnVirtualThread
    @Override
    public Uni<Empty> changeTrackInfo(TrackInfo request) {
        try {
            trackService.updateTrackName(request.getTrackId(), request.getName());
            trackInfoEmitters.forEach(emitter -> emitter.emit(request));
            return Uni.createFrom().item(Empty.getDefaultInstance());
        } catch (IllegalArgumentException e) {
            System.err.println("Error renaming track id " + request.getTrackId() + " to " + request.getName() + " : " + e.getMessage());
            return Uni.createFrom().failure(e);
        }
    }

    // Test command: grpcurl -plaintext localhost:9000 edit.EditService/GetSamplePositions
    @Override
    public Multi<SampleInfo> getSamplePositions(Empty request) {
        return Multi.createFrom().emitter(emitter -> {
            samplePositionEmitters.add(emitter);
            emitter.onTermination(() -> samplePositionEmitters.remove(emitter));
        });
    }

    // Test command: grpcurl -plaintext localhost:9000 edit.EditService/GetUpdatedTracks
    @Override
    public Multi<TrackInfo> getUpdatedTracks(Empty request) {
        return Multi.createFrom().emitter(emitter -> {
            trackInfoEmitters.add(emitter);
            emitter.onTermination(() -> trackInfoEmitters.remove(emitter));
        });
    }

    // Test command: grpcurl -plaintext localhost:9000 edit.EditService/GetSampleUploads
    @Override
    public Multi<SampleInfo> getSampleUploads(Empty request) {
        return Multi.createFrom().emitter(emitter -> {
            sampleRemoveEmitters.add(emitter);
            emitter.onTermination(() -> sampleRemoveEmitters.remove(emitter));
        });
    }
}
