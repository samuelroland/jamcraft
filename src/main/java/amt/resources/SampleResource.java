package amt.resources;

import amt.dto.SampleDTO;
import amt.dto.UserDTO;
import amt.services.SampleService;
import amt.services.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("samples")
public class SampleResource {

    @Inject
    SampleService sampleService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSamples() {
        return Response.ok(sampleService.getAllSamples()).build();
    }

//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response userJoin(UserDTO userDTO) {
//        var user = new UserDTO(null, userDTO.name(), null);
//        return Response.ok(userService.saveUser(user)).build();
//    }

    // TODO Routes: Track:
    //  GET getAllTrack qui contient les samples nested
    //  POST upload des samples depuis webapp
    //  Notification par JMS (new user / à voir)
    //  Export du projet format mp3 => Route /export (exportResource)
    //
}