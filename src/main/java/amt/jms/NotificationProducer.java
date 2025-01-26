package amt.jms;

import amt.dto.UserDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;

/**
 * A JMS producer that sends user notifications to the "notifications" queue.
 * This producer allows other components to broadcast notifications by sending
 * {@link UserDTO} objects to the JMS queue.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@ApplicationScoped
public class NotificationProducer {

    @Inject
    ConnectionFactory connectionFactory;

    /**
     * Sends a {@link UserDTO} notification to the "notifications" queue.
     *
     * @param user the {@link UserDTO} object containing user information to be sent.
     */
    public void sendNotification(UserDTO user) {
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            context.createProducer().send(context.createQueue("notifications"), user);
        }
    }
}
