package amt.jms;

import amt.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import org.jboss.logging.Logger;
import io.smallrye.mutiny.Multi;

/**
 * A JMS consumer that listens for user notifications on the "notifications" queue.
 * This consumer receives notifications as {@link UserDTO} objects and provides a reactive stream of notifications
 * for other components to subscribe to using Mutiny's {@link Multi}.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@ApplicationScoped
public class NotificationConsumer {

    @Inject
    ConnectionFactory connectionFactory;

    private JMSContext context;
    private JMSConsumer consumer;

    private static final Logger LOG = Logger.getLogger(NotificationConsumer.class);

    /**
     * Initializes the JMS context and consumer for the "notifications" queue.
     * This method is called automatically after the bean is constructed.
     */
    @PostConstruct
    public void start() {
        context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE);
        consumer = context.createConsumer(context.createQueue("notifications"));
    }

    /**
     * Provides a reactive stream of {@link UserDTO} notifications.
     *
     * @return a {@link Multi} stream of {@link UserDTO} notifications.
     */
    public Multi<UserDTO> getNotificationStream() {
        // Create a Multi stream from JMS consumer
        return Multi.createFrom().emitter(emitter -> {
            new Thread(() -> {
                try {
                    while (true) {
                        UserDTO user = consumer.receiveBody(UserDTO.class);
                        LOG.info("Received notification: " + user);
                        emitter.emit(user);
                    }
                } catch (Exception e) {
                    LOG.error("Error receiving message", e);
                    emitter.fail(e);
                }
            }).start();
        });
    }

    /**
     * Closes the JMS context and cleans up resources.
     * This method is called automatically before the bean is destroyed.
     */
    @PreDestroy
    public void stop() {
        if (context != null) {
            context.close();
        }
    }
}
