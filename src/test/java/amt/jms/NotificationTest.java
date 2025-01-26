package amt.jms;

import amt.dto.UserDTO;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class NotificationTest {

    @Inject
    NotificationProducer producer;

    @ConfigProperty(name = "notification.queue.name")
    String queueName;

    @Inject
    ConnectionFactory connectionFactory;

    @Test
    public void testNotification() {
        // Create a temporary consumer to validate the message
        try (JMSContext context = connectionFactory.createContext()) {
            Queue queue = context.createQueue(queueName);
            JMSConsumer consumer = context.createConsumer(queue);

            // Send a message
            UserDTO user  = new UserDTO(1, "John", LocalDateTime.now());
            producer.sendNotification(user);

            // Receive the message
            UserDTO receivedUser = consumer.receiveBody(UserDTO.class, 2000);

            // Assert that the received message matches the sent message
            assertEquals(user, receivedUser, "The received message should match the sent message");
        }
    }
}
