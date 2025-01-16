package amt.grpc;

import amt.*;
import amt.dto.UserDTO;
import amt.services.UserService;
import com.google.protobuf.Empty;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.MultiEmitter;
import jakarta.inject.Inject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@GrpcService
public class UsersGrpcService implements UsersService {

    @Inject
    UserService userService;

    // Active emitters for streaming sample positions
    private final CopyOnWriteArrayList<MultiEmitter<? super UserChange>> usersEmitters = new CopyOnWriteArrayList<>();

    // Test command: grpcurl -plaintext -d '{\"name\": \"Bob\"}' localhost:9000 users.UsersService/Join
    @RunOnVirtualThread
    @Override
    public Uni<UsersList> join(UserName request) {
        try {
            // Validate username length
            String username = request.getName();
            if (username.length() > 64) {
                throw new IllegalArgumentException("Username cannot be longer than 64 characters");
            }

            UserDTO newUser = userService.saveUser(new UserDTO(null, username, null));

            // Notify all connected clients about the new user
            UserChange userChange = UserChange.newBuilder()
                    .setAction(SessionAction.JOIN)
                    .setUserId(newUser.id())
                    .setName(newUser.name())
                    .build();

            usersEmitters.forEach(emitter -> emitter.emit(userChange));

            List<UserDTO> users = userService.getAllUsers();

            // Build and return the UsersList response
            UsersList usersList = UsersList.newBuilder()
                    .addAllUsers(users.stream()
                            .map(user -> User.newBuilder()
                                    .setId(user.id())
                                    .setName(user.name())
                                    .build())
                            .toList())
                    .build();
            return Uni.createFrom().item(usersList);
        } catch (IllegalArgumentException e){
            System.err.println("Failed to join: " + e.getMessage());
            // Return an empty UsersList or fail the Uni based on your error-handling approach
            return Uni.createFrom().failure(e);
        }
    }

    // Test command: grpcurl -plaintext localhost:9000 users.UsersService/GetUsersEvents
    @Override
    public Multi<UserChange> getUsersEvents(Empty request) {
        return Multi.createFrom().emitter(emitter -> {
            usersEmitters.add(emitter);
            emitter.onTermination(() -> usersEmitters.remove(emitter));
        });
    }
}
