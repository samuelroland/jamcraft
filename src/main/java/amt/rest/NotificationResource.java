package amt.rest;

import amt.jms.NotificationConsumer;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("notifications")
public class NotificationResource {

    @Inject
    NotificationConsumer notificationConsumer;

    @Inject
    Sse sse;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @GET
    @Path("stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void streamNotifications(SseEventSink sseEventSink) {
        executorService.execute(() -> {
            try {
                var notifications = notificationConsumer.getNotifications();
                while (true) {
                    String message = notifications.take();
                    sseEventSink.send(sse.newEvent(message));
                }
            } catch (Exception e) {
                sseEventSink.close();
            }
        });
    }
}
