package amt.jms;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import org.jboss.logging.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@ApplicationScoped
public class NotificationConsumer {

    @Inject
    ConnectionFactory connectionFactory;

    private JMSContext context;
    private JMSConsumer consumer;
    private final BlockingQueue<String> notifications = new LinkedBlockingQueue<>();
    private static final Logger LOG = Logger.getLogger(NotificationConsumer.class);

    @PostConstruct
    public void start() {
        context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE);
        consumer = context.createConsumer(context.createQueue("notifications"));

        // Start a thread to listen for messages
        new Thread(() -> {
            while (true) {
                try {
                    String message = consumer.receiveBody(String.class);
                    LOG.info("Received notification: " + message);
                    notifications.offer(message);
                } catch (Exception e) {
                    LOG.error("Error receiving message", e);
                }
            }
        }).start();
    }

    public BlockingQueue<String> getNotifications() {
        return notifications;
    }

    @PreDestroy
    public void stop() {
        if (context != null) {
            context.close();
        }
    }
}
