package amt.grpc;

import amt.*;
import com.google.protobuf.Empty;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@GrpcService
public class EditGrpcService implements EditService {

    // Unary sending to let clients change position of samples
    @Override
    public Uni<Empty> changeSamplePosition(SamplePosition request) {
        return null;
    }

    // Unary sending to let clients remove a sample from the project, giving the samples_tracks.id !
    @Override
    public Uni<Empty> removeSample(SampleInstanceId request) {
        return null;
    }

    // Unary sending to update track info or create one
    @Override
    public Uni<Empty> changeTrackInfo(TrackInfo request) {
        return null;
    }

    // Server-side streaming to broadcast positions of samples changed by others
    @Override
    public Multi<SamplePosition> getSamplePositions(Empty request) {
        return null;
    }

    // Server-side streaming to broadcast updated tracks, it allows to update track names in live
    @Override
    public Multi<TrackInfo> getUpdatedTracks(Empty request) {
        return null;
    }

    // Server-side streaming to broadcast new sample uploads, useful to update the local library
    @Override
    public Multi<SampleInfo> getSampleUploads(Empty request) {
        return null;
    }
}
