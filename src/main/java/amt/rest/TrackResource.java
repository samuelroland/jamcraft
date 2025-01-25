package amt.rest;

import amt.services.TrackService;
import jakarta.ws.rs.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

/**
 * REST endpoint for managing track-related operations.
 * Provides endpoints to retrieve tracks along with their associated sample instances.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@Path("tracks")
public class TrackResource {

    @Inject
    TrackService trackService;

    /**
     * Retrieves all tracks with their associated sample instances.
     *
     * @return a {@link Response} containing the list of all tracks with sample instances in JSON format
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTracksWithSampleInstances() {
        return Response.ok(trackService.getAllTracksWithSamples()).build();
    }
}
