package amt.jms;

import amt.dto.UserDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class NotificationProducer {

    @Inject
    ConnectionFactory connectionFactory;

    @ConfigProperty(name = "notification.queue.name")
    String queueName;

    private static final Logger LOG = Logger.getLogger(NotificationProducer.class);

    public void sendNotification(UserDTO user) {
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            context.createProducer().send(context.createQueue(queueName), user);
            LOG.info("Notification sent to queue '" + queueName + "' for user: " + user);
        } catch (Exception e) {
            LOG.error("Failed to send notification to queue '" + queueName + "' for user: " + user, e);
        }
    }
}
