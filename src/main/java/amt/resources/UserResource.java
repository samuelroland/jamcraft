package amt.resources;

import amt.dto.UserDTO;
import amt.jms.NotificationProducer;
import amt.services.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("users")
public class UserResource {

    @Inject
    UserService userService;

    @Inject
    NotificationProducer notificationProducer;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        return Response.ok(userService.getAllUsers()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userJoin(UserDTO userDTO) {
        var user = new UserDTO(null, userDTO.name(), null);
        // Send a notification
        notificationProducer.sendNotification("User " + userDTO.name() + " joined session.");
        return Response.ok(userService.saveUser(user)).build();
    }
}
