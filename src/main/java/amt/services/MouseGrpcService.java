package amt.services;

import amt.MousePosition;
import amt.MouseService;
import com.google.protobuf.Empty;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.MultiEmitter;
import java.util.concurrent.CopyOnWriteArrayList;

@GrpcService
public class MouseGrpcService implements MouseService {


    // A thread-safe list of active emitters for streaming updates
    private final CopyOnWriteArrayList<MultiEmitter<? super MousePosition>> emitters = new CopyOnWriteArrayList<>();

    @Override
    public Uni<Empty> sendMousePosition(MousePosition position) {
        // Broadcast the received position to all active streams
        emitters.forEach(emitter -> {
            try {
                emitter.emit(position);
            } catch (Exception e) {
                System.err.println("Error emitting to a client: " + e.getMessage());
            }
        });

        return Uni.createFrom().item(Empty.getDefaultInstance());
    }

    @Override
    public Multi<MousePosition> getMouseUpdates(Empty request) {
        return Multi.createFrom().emitter(emitter -> {
            // Add the emitter to the active list
            emitters.add(emitter);

            // Remove the emitter when the client disconnects
            emitter.onTermination(() -> {
                System.out.println("Client disconnected from MouseUpdates");
                emitters.remove(emitter);
            });
        });
    }
}
