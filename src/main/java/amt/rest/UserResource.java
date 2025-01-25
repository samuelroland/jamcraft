package amt.rest;

import amt.jms.NotificationProducer;
import amt.services.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST endpoint for managing user-related operations.
 * Provides functionality for users to leave a session and handles notifications.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@Path("leave")
public class UserResource {

    @Inject
    UserService userService;

    @Inject
    NotificationProducer notificationProducer;

    private final Gson gson = new Gson();

    /**
     * Handles the removal of a user from the session.
     * Notifies other users about the departure.
     * Example request:
     * {@code curl -X POST http://localhost:8080/leave -H "Content-Type: text/plain" -d '{"id": "1", "name": "spy"}'}
     *
     * @param request a JSON string containing the user details (e.g., ID and name)
     * @return a {@link Response} indicating the success or failure of the operation
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response userLeave(String request) {
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid user data\"}")
                    .build();
        }
        try {
            // Parse the JSON string into a JsonObject
            JsonObject jsonObject = gson.fromJson(request, JsonObject.class);
            Integer parsedId = jsonObject.has("id") ? jsonObject.get("id").getAsInt() : null;
            if (parsedId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Missing or invalid id\"}")
                        .build();
            }

            var user = userService.deleteUser(parsedId);
            notificationProducer.sendNotification(user);
            return Response.ok("{\"status\": \"User " + user.name() + " removed successfully\"}").build();
        } catch (Exception e) {
            System.err.println("Failed to leave: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
