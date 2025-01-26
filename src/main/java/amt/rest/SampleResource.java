package amt.rest;

import java.io.IOException;

import amt.dto.SampleDTO;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.*;
import amt.services.SampleService;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("samples")
public class SampleResource {

    // TODO: make sure this folder works in production too
    private static final String AUDIO_STORAGE_FOLDER = "audio/";

    // As a simplification, we decided to be Linux/Mac specific, and install
    // ffmpeg+ffprobe in the docker image for production deployment
    private static final String FFPROBE_PATH = "/usr/bin/ffprobe";

    @Inject
    SampleService sampleService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSamples() {
        return Response.ok(sampleService.getAllSamples()).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadSample(FileUploadInput form) {
        if (form.name == null || form.name.trim().isEmpty()) {
            throw new BadRequestException("Name is required and must be not empty");
        }
        if (form.file == null) {
            throw new BadRequestException("File is not provided");
        }

        try {
            var filename = form.file.fileName();
            var file = form.file.filePath().toFile();
            if (!filename.endsWith(".mp3")) {
                throw new BadRequestException("File must ends with .mp3 extension, other formats are not supported");
            }
            var fileDestination = AUDIO_STORAGE_FOLDER + filename;
            Files.move(Paths.get(file.getPath()), Paths.get(fileDestination),
                    StandardCopyOption.REPLACE_EXISTING);
            var name = form.name;
            // TODO: do not hardcode path to ffprobe for windows dev
            FFprobe ffprobe = new FFprobe(FFPROBE_PATH);
            FFmpegProbeResult probeResult = ffprobe
                    .probe(fileDestination);

            FFmpegFormat format = probeResult.getFormat();

            if (!format.format_name.equals("mp3")) {
                Files.delete(Paths.get(fileDestination));
                throw new BadRequestException("Unsupported format, only mp3 format is accepted.");
            }

            var finalSample = new SampleDTO(null, name, filename, format.duration, null);
            return Response.ok(sampleService.saveSample(finalSample)).build();
        } catch (IOException e) {
            System.err.println(e.toString());
            return Response.serverError().build();
        }
    }

    public static class FileUploadInput {
        @FormParam("name")
        public String name;

        @FormParam("file")
        public FileUpload file;

    }
}
