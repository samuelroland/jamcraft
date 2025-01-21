package amt.grpc;

import amt.MousePosition;
import amt.MouseService;
import amt.MouseSubscription;
import com.google.protobuf.Empty;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

@GrpcService
public class MouseGrpcService implements MouseService {

    // BroadcastProcessor for hot streaming of mouse positions
    private final BroadcastProcessor<MousePosition> processor = BroadcastProcessor.create();

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

    // Test command:
    @Override
    public Multi<MousePosition> getMouseUpdates(MouseSubscription request) {
        int userId = request.getUserId();

        // Return a filtered stream: exclude updates from the current user
        return processor.filter(position -> position.getUserId() != userId);
    }
}
