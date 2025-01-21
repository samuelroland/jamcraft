package amt.grpc;

import amt.*;
import amt.dto.UserDTO;
import amt.jms.NotificationConsumer;
import amt.services.UserService;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

@GrpcService
public class UsersGrpcService implements UsersService {

    @Inject
    UserService userService;

    @Inject
    NotificationConsumer notificationConsumer;

    // BroadcastProcessor for hot streaming user updates
    private final BroadcastProcessor<UserChange> processor = BroadcastProcessor.create();


    @PostConstruct
    public void startListeningToNotifications() {
        // Subscribe to the NotificationConsumer's stream
        notificationConsumer.getNotificationStream()
                .subscribe().with(user -> {
                    // Convert the notification to a UserChange
                    UserChange userChange = UserChange.newBuilder()
                            .setAction(SessionAction.LEAVE)
                            .setUserId(user.id())
                            .setName(user.name())
                            .build();

                    // Emit the UserChange to all connected clients
                    processor.onNext(userChange);
                });
    }

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

            var newUser = userService.saveUser(new UserDTO(null, username, null));

            // Notify all connected clients about the new user
            UserChange userChange = UserChange.newBuilder()
                    .setAction(SessionAction.JOIN)
                    .setUserId(newUser.id())
                    .setName(newUser.name())
                    .build();

            processor.onNext(userChange);
            return Uni.createFrom().item(getUsersList());
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to join: " + e.getMessage());
            return Uni.createFrom().failure(e);
        }
    }

    // Test command: grpcurl -plaintext -d '{\"userId\": 2}' localhost:9000 users.UsersService/GetUsersEvents
    @Override
    public Multi<UserChange> getUsersEvents(UserSubscription request) {
        int id = request.getUserId();
        return processor.filter(user -> user.getUserId() != id);
    }

    private UsersList getUsersList() {
        // Build and return the UsersList response
        return UsersList.newBuilder()
                .addAllUsers(userService.getAllUsers().stream()
                        .map(user -> User.newBuilder()
                                .setId(user.id())
                                .setName(user.name())
                                .build())
                        .toList())
                .build();
    }
}
