package amt.repositories;

import amt.entities.User;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class UserRepositoryTest {

    @Inject
    UserRepository userRepository;

    @Test
    public void testSaveAndFindById() {
        User user = new User();
        user.setName("John Doe");

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId(), "Saved user's ID should not be null.");
        assertEquals("John Doe", savedUser.getName(), "User's name should match.");

        User retrievedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(retrievedUser, "User should be retrievable by ID.");
        assertEquals(savedUser.getId(), retrievedUser.getId(), "IDs should match.");
    }

    @Test
    public void testFindAll() {
        List<User> users = userRepository.findAll();
        assertNotNull(users, "Users list should not be null.");
    }

    @Test
    public void testDeleteById() {
        User user = new User();
        user.setName("Jane Doe");

        User savedUser = userRepository.save(user);
        Integer userId = savedUser.getId();

        userRepository.deleteById(userId);

        assertFalse(userRepository.findById(userId).isPresent(), "User should be deleted.");
    }
}
