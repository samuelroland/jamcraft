package amt.utils;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class AudioAccess {

    private static final Logger LOGGER = Logger.getLogger(AudioAccess.class);
    private static final String UPLOAD_DIR = "audio";

    @Inject
    Vertx vertx;

    // Route /audio/* requests to local "audio" folder to directly get audio files
    public void init(@Observes Router router) {
        router.route("/audio/*").handler(StaticHandler.create(UPLOAD_DIR).setCachingEnabled(true));
        LOGGER.info("Static file handler configured for /audio/*");
    }
}
