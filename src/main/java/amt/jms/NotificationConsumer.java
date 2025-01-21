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
import io.smallrye.mutiny.unchecked.Unchecked;

@ApplicationScoped
public class NotificationConsumer {

    @Inject
    ConnectionFactory connectionFactory;

    private JMSContext context;
    private JMSConsumer consumer;

    private static final Logger LOG = Logger.getLogger(NotificationConsumer.class);

    @PostConstruct
    public void start() {
        context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE);
        consumer = context.createConsumer(context.createQueue("notifications"));
    }

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

    @PreDestroy
    public void stop() {
        if (context != null) {
            context.close();
        }
    }
}
