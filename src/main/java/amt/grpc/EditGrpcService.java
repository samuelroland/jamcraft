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

/**
 * A gRPC service for managing live editing of tracks and samples.
 * This service allows users to perform real-time updates to tracks and samples,
 * including adding, removing, and renaming tracks, as well as changing sample positions.
 * It uses a {@link BroadcastProcessor} to broadcast changes to all connected clients.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@GrpcService
public class EditGrpcService implements EditService {

    @Inject
    TrackService trackService;
    @Inject
    SampleTrackService sampleTrackService;

    /**
     * A hot stream processor for broadcasting updates to all subscribers.
     */
    private final BroadcastProcessor<SampleInfo> processor = BroadcastProcessor.create();


    /**
     * Updates the position of a sample or creates a new sample track if it doesn't exist.
     * Test command: grpcurl -plaintext -d '{\"sampleId\": 3, \"instanceId\": 8, \"startTime\": 42.42, \"trackId\": 4, \"userId\": 2}' localhost:9000 edit.EditService/ChangeSamplePosition
     *
     * @param request the {@link SampleInfo} containing details of the sample update.
     * @return a {@link Uni} with an empty response if the operation succeeds, or a failure if it fails.
     */
    @Override
    @RunOnVirtualThread
    public Uni<Empty> changeSamplePosition(SampleInfo request) {
        try {
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

            // Create the response and broadcast
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

    /**
     * Removes a sample track from the database.
     * Test command : grpcurl -plaintext -d '{\"instanceId\": 1, \"userId\": 2 }' localhost:9000 edit.EditService/RemoveSample
     *
     * @param request the {@link SampleInstanceId} containing the ID of the sample track to remove.
     * @return a {@link Uni} with an empty response if the operation succeeds, or a failure if it fails.
     */
    @Override
    @RunOnVirtualThread
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

    /**
     * Updates the name of an existing track.
     * Test command: grpcurl -plaintext -d '{\"trackId\": 3, \"name\": \"Drums\", \"userId\": 8 }' localhost:9000 edit.EditService/UpdateTrackName
     *
     * @param request the {@link TrackInfo} containing the ID of the track and its new name.
     * @return a {@link Uni} with an empty response if the operation succeeds, or a failure if it fails.
     */
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

    /**
     * Adds a new track to the database.
     * Test command: grpcurl -plaintext -d '{\"name\": \"Test track\", \"userId\": 8}' localhost:9000 edit.EditService/AddTrack
     *
     * @param request the {@link TrackName} containing the name of the new track.
     * @return a {@link Uni} with an empty response if the operation succeeds, or a failure if it fails.
     */
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

    /**
     * Provides a real-time stream of edit events to all subscribers.
     * Clients can subscribe to this method to receive updates about edits
     * made by other users, excluding their own updates.
     * Test command: grpcurl -plaintext -d '{\"id\": 1}' localhost:9000 edit.EditService/GetEditEvents
     *
     * @param request the {@link UserId} containing the ID of the requesting user.
     * @return a {@link Multi} stream of {@link SampleInfo} updates.
     */
    @Override
    @RunOnVirtualThread
    public Multi<SampleInfo> getEditEvents(UserId request) {
        int id = request.getId();
        return processor.filter(user -> user.getUserId() != id);
    }
}
