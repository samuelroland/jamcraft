package amt.jms;

import amt.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import io.smallrye.mutiny.Multi;

@ApplicationScoped
public class NotificationConsumer {

    @Inject
    ConnectionFactory connectionFactory;

    @ConfigProperty(name = "notification.queue.name")
    String queueName;


    private JMSContext context;
    private JMSConsumer consumer;

    private static final Logger LOG = Logger.getLogger(NotificationConsumer.class);

    @PostConstruct
    public void start() {
        context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE);
        consumer = context.createConsumer(context.createQueue(queueName));
        LOG.info(getClass().getSimpleName() + " started. Listening to queue: " + queueName);
    }

    public Multi<UserDTO> getNotificationStream() {
        return Multi.createFrom().emitter(emitter -> {
            Runnable task = () -> {
                try {
                    while (true) {
                        UserDTO user = consumer.receiveBody(UserDTO.class);
                        if (user != null) {
                            LOG.info("Received notification: " + user);
                            emitter.emit(user);
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Error receiving message", e);
                    emitter.fail(e);
                }
            };
            Thread thread = new Thread(task, "NotificationConsumerThread");
            thread.setDaemon(true);
            thread.start();
        });
    }

    @PreDestroy
    public void stop() {
        if (context != null) {
            context.close();
            LOG.info("NotificationConsumer stopped.");
        }
    }
}

