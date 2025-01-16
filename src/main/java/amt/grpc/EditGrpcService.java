package amt.grpc;

import amt.*;
import amt.services.SampleService;
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
    SampleService sampleService; // Handles DB operations for samples
    @Inject
    TrackService trackService; // Handles DB operations for tracks

    // Active emitters for streaming sample positions
    private final CopyOnWriteArrayList<MultiEmitter<? super SamplePosition>> samplePositionEmitters = new CopyOnWriteArrayList<>();

    // Active emitters for streaming track info updates
    private final CopyOnWriteArrayList<MultiEmitter<? super TrackInfo>> trackInfoEmitters = new CopyOnWriteArrayList<>();

    // Active emitters for streaming sample uploads
    private final CopyOnWriteArrayList<MultiEmitter<? super SampleInfo>> sampleUploadEmitters = new CopyOnWriteArrayList<>();

    // Handle incoming requests to change sample position
    @RunOnVirtualThread
    @Override
    public Uni<Empty> changeSamplePosition(SamplePosition request) {

        // TODO Update the SampleTrack
//        var sampleTrack = sampleService.getSampleById(request.getId());
//        sample.
//        sampleService.saveSample(sample);
        // Notify all subscribers of the new sample position
        samplePositionEmitters.forEach(emitter -> emitter.emit(request));

        // Return a successful response
        return Uni.createFrom().item(Empty.getDefaultInstance());
    }

    // Handle incoming requests to remove a sample
    @Override
    public Uni<Empty> removeSample(SampleInstanceId request) {
        // In a real implementation, handle removal logic here
        System.out.println("Removing sample with instance ID: " + request.getInstanceId());
        return Uni.createFrom().item(Empty.getDefaultInstance());
    }

    // Handle incoming requests to update track info
    @Override
    public Uni<Empty> changeTrackInfo(TrackInfo request) {
        // Notify all subscribers of the updated track info
        trackInfoEmitters.forEach(emitter -> emitter.emit(request));

        // Return a successful response
        return Uni.createFrom().item(Empty.getDefaultInstance());
    }

    // Stream sample positions to clients
    @Override
    public Multi<SamplePosition> getSamplePositions(Empty request) {
        return Multi.createFrom().emitter(emitter -> {
            samplePositionEmitters.add(emitter);
            emitter.onTermination(() -> samplePositionEmitters.remove(emitter));
        });
    }

    // Stream updated track info to clients
    @Override
    public Multi<TrackInfo> getUpdatedTracks(Empty request) {
        return Multi.createFrom().emitter(emitter -> {
            trackInfoEmitters.add(emitter);
            emitter.onTermination(() -> trackInfoEmitters.remove(emitter));
        });
    }

    // Stream uploaded sample info to clients
    @Override
    public Multi<SampleInfo> getSampleUploads(Empty request) {
        return Multi.createFrom().emitter(emitter -> {
            sampleUploadEmitters.add(emitter);
            emitter.onTermination(() -> sampleUploadEmitters.remove(emitter));
        });
    }
}
