package amt.rest;

import amt.dto.UserDTO;
import amt.jms.NotificationProducer;
import amt.services.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("leave")
public class UserResource {

    @Inject
    UserService userService;

    @Inject
    NotificationProducer notificationProducer;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    // Test command: curl -X POST http://localhost:8080/leave -H "Content-Type: application/json" -d '{"id": "1", "name": "spy"}'
    public Response userLeave(UserDTO user) {
        if (user == null || user.id() == null || user.name() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid user data\"}")
                    .build();
        }
        try {
            userService.deleteUser(user.id());
            notificationProducer.sendNotification(user.name());
            return Response.ok("{\"status\": \"User " + user.name() + " removed successfully\"}").build();
        } catch (Exception e) {
            System.err.println("Failed to leave: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
