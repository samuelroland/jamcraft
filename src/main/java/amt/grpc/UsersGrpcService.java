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

/**
 * A gRPC service for managing user session events in real-time.
 * This service handles user-related actions such as joining or leaving a session,
 * and broadcasts updates to all connected clients using a {@link BroadcastProcessor}.
 * It integrates with the {@link NotificationConsumer} to react to notifications and
 * notifies clients about user session changes.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@GrpcService
public class UsersGrpcService implements UsersService {

    @Inject
    UserService userService;

    @Inject
    NotificationConsumer notificationConsumer;

    /**
     * A hot stream processor for broadcasting user session changes to all connected clients.
     */
    private final BroadcastProcessor<UserChange> processor = BroadcastProcessor.create();


    /**
     * Initializes the service by subscribing to the {@link NotificationConsumer}'s notification stream.
     */
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

    /**
     * Handles a new user joining the session.
     * Test command: grpcurl -plaintext -d '{\"name\": \"Bob\"}' localhost:9000 users.UsersService/Join
     *
     * @param request the {@link UserName} containing the name of the user joining the session.
     * @return a {@link Uni} containing the updated {@link UsersList} with all users in the session.
     */
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

    /**
     * Subscribes a client to real-time user session events.
     * Test command: grpcurl -plaintext -d '{\"userId\": 2}' localhost:9000 users.UsersService/GetUsersEvents
     *
     * @param request the {@link UserSubscription} containing the ID of the subscribing user.
     * @return a {@link Multi} stream of {@link UserChange} events for other users.
     */
    @Override
    public Multi<UserChange> getUsersEvents(UserSubscription request) {
        int id = request.getUserId();
        return processor.filter(user -> user.getUserId() != id);
    }

    /**
     * Retrieves the current list of users in the session.
     *
     * @return a {@link UsersList} containing all users in the session.
     */
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
