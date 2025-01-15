package amt.rest;

import amt.services.TrackService;
import jakarta.ws.rs.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("tracks")
public class TrackResource {

    @Inject
    TrackService trackService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTracksWithSampleInstances() {
        return Response.ok(trackService.getAllTracksWithSamples()).build();
    }
}
