package amt.grpc;

import amt.MousePosition;
import amt.MouseService;
import amt.MouseSubscription;
import com.google.protobuf.Empty;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

/**
 * A gRPC service for handling real-time mouse position updates.
 * This service enables clients to send their current mouse positions and receive updates
 * about mouse positions from other users in real time. It uses a {@link BroadcastProcessor}
 * to manage and broadcast updates to all subscribed clients.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@GrpcService
public class MouseGrpcService implements MouseService {

    /**
     * A hot stream processor that broadcasts mouse position updates to all subscribers.
     */
    private final BroadcastProcessor<MousePosition> processor = BroadcastProcessor.create();

    /**
     * Receives and broadcasts a mouse position update from a client.
     *
     * @param position the {@link MousePosition} sent by the client.
     * @return a {@link Uni} wrapping an empty response to acknowledge receipt of the position.
     * @see amt.MousePosition
     */
    @Override
    public Uni<Empty> sendMousePosition(MousePosition position) {
        // Emit the new mouse position to the processor
        try {
            processor.onNext(position);
        } catch (Exception e) {
            System.err.println("Error broadcasting mouse position: " + e.getMessage());
        }

        return Uni.createFrom().item(Empty.getDefaultInstance());
    }

    /**
     * Provides a real-time stream of mouse position updates, excluding updates from the requesting user.
     * Test command: grpcurl -plaintext  -d '{\"userId\": 2}'localhost:9000 mouse.MouseService/GetMouseUpdates
     *
     * @param request the {@link MouseSubscription} request from the client, containing the user ID of the subscriber.
     * @return a {@link Multi} stream of {@link MousePosition} updates from other users.
     */
    @Override
    public Multi<MousePosition> getMouseUpdates(MouseSubscription request) {
        int userId = request.getUserId();

        // Return a filtered stream: exclude updates from the current user
        return processor.filter(position -> position.getUserId() != userId);
    }
}
