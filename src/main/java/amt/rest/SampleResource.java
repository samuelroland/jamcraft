package amt.rest;

import java.io.IOException;
import java.time.LocalDateTime;

import amt.dto.SampleDTO;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegStream;
import amt.services.SampleService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadSample(SampleDTO sampleDTO) {
        String filename = "f-r-a-g-i-l-e__lotus-guzheng-chops-cm.mp3";

        try {
            // TODO: do not hardcode path to ffprobe for windows dev
            FFprobe ffprobe = new FFprobe("/usr/bin/ffprobe");
            // TODO: make sure this folder works in production too
            FFmpegProbeResult probeResult = ffprobe.probe("build/resources/main/META-INF/resources/audio/" + filename);

            FFmpegFormat format = probeResult.getFormat();
            // ffprobe -v quiet -print_format compact=print_section=0:nokey=1:escape=csv
            // -show_entries format=duration audio.mp3
            var finalSample = new SampleDTO(null, sampleDTO.name(), sampleDTO.filepath(), format.duration, null);
            return Response.ok(sampleService.saveSample(finalSample)).build();
        } catch (IOException e) {
            System.err.println(e.toString());
            return Response.serverError().build();
        }
    }

    // TODO Routes: Track:
    // POST upload des samples depuis webapp
    // Export du projet format mp3 => Route /export (exportResource)
}
