package amt.services;

import amt.MousePosition;
import amt.MouseService;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.MultiEmitter;

import java.util.concurrent.ConcurrentHashMap;

@GrpcService
public class MouseGrpcService implements MouseService {

    // Map to associate each user ID with their emitter
    private final ConcurrentHashMap<Integer, MultiEmitter<? super MousePosition>> emitters = new ConcurrentHashMap<>();

    @Override
    public Multi<MousePosition> realTimeMouseSync(Multi<MousePosition> request) {
        // Create a Multi to broadcast positions to the current client
        return Multi.createFrom().emitter(emitter -> {
            // Use the user ID from the first message to identify the client
            request.subscribe().with(
                    mousePosition -> {
                        int userId = mousePosition.getUserId();

                        // Register the client (if not already registered)
                        emitters.putIfAbsent(userId, emitter);

                        // Broadcast the position to all connected clients
                        broadcastToAll(mousePosition);
                    },
                    failure -> System.err.println("Error in client stream: " + failure),
                    () -> {
                        System.out.println("Client disconnected");
                        // Remove the client from the map on disconnection
                        emitters.values().remove(emitter);
                    }
            );

            // Clean up the client on termination
            emitter.onTermination(() -> emitters.values().remove(emitter));
        });
    }

    private void broadcastToAll(MousePosition mousePosition) {
        emitters.forEach((userId, emitter) -> {
            try {
                if (mousePosition.getUserId() != userId) {
                    emitter.emit(mousePosition);
                }
            } catch (Exception e) {
                System.err.println("Error broadcasting to user " + userId + ": " + e.getMessage());
            }
        });
    }
}
