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

/**
 * REST resource for managing audio samples.
 * Provides endpoints for retrieving all audio samples and uploading new audio samples.
 * Uploaded files are validated for the MP3 format, stored on the server, and their metadata is processed using FFprobe.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@Path("samples")
public class SampleResource {

    /**
     * Static directory where uploaded audio files are stored.
     */
    private static final String AUDIO_STORAGE_FOLDER = "audio/";

    /**
     * Path to the FFprobe binary for audio metadata processing.
     * This is specific to Linux/Mac environments and requires FFprobe to be installed on the system
     */
    private static final String FFPROBE_PATH = "/usr/bin/ffprobe";

    @Inject
    SampleService sampleService;


    /**
     * Retrieves a list of all audio samples.
     *
     * @return A {@link Response} containing a JSON array of all audio samples.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSamples() {
        return Response.ok(sampleService.getAllSamples()).build();
    }

    /**
     * Uploads a new audio sample.
     * Validates the uploaded file to ensure it is in MP3 format, saves it to the server, and extracts its metadata using FFprobe.
     *
     * @param form The form data containing the audio file and its associated name.
     * @return A {@link Response} containing the saved {@link SampleDTO} if successful.
     * @throws BadRequestException If the file or name is invalid or unsupported.
     */
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
            var path = Paths.get(fileDestination);
            Files.move(Paths.get(file.getPath()), path, StandardCopyOption.REPLACE_EXISTING);
            FFprobe ffprobe = new FFprobe(FFPROBE_PATH);
            FFmpegProbeResult probeResult = ffprobe.probe(fileDestination);

            FFmpegFormat format = probeResult.getFormat();

            if (!format.format_name.equals("mp3")) {
                Files.delete(path);
                throw new BadRequestException("Unsupported format, only mp3 format is accepted.");
            }

            var finalSample = new SampleDTO(null, form.name, filename, format.duration, null);
            return Response.ok(sampleService.saveSample(finalSample)).build();
        } catch (IOException e) {
            System.err.println("Error during file upload: " + e.getMessage());
            return Response.serverError().entity("Internal server error. Please try again.").build();
        }
    }

    /**
     * Inner class representing the input form data for file uploads.
     */
    public static class FileUploadInput {
        /**
         * The name of the audio sample.
         */
        @FormParam("name")
        public String name;

        /**
         * The uploaded file containing the audio sample.
         */
        @FormParam("file")
        public FileUpload file;
    }
}
