package amt.jms;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;

@ApplicationScoped
public class NotificationProducer {

    @Inject
    ConnectionFactory connectionFactory;

    public void sendNotification(String message) {
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            context.createProducer().send(context.createQueue("notifications"), message);
        }
    }
}
