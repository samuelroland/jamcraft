package amt.services;

import amt.dto.UserDTO;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class UserServiceTest {

    @Inject
    UserService userService;

    @Test
    public void testSaveUser() {
        UserDTO userDTO = new UserDTO(null, "Alice", null);

        UserDTO savedUser = userService.saveUser(userDTO);

        assertNotNull(savedUser.id(), "Saved user's ID should not be null.");
        assertEquals("Alice", savedUser.name(), "User's name should match.");
    }

    @Test
    public void testGetAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        assertNotNull(users, "Users list should not be null.");
        assertNotNull(users.getFirst(), "Users list should have zero or more entries.");
    }

    @Test
    public void testDeleteUser() {
        UserDTO userDTO = new UserDTO(null, "Bob", null);
        UserDTO savedUser = userService.saveUser(userDTO);

        UserDTO deletedUser = userService.deleteUser(savedUser.id());

        assertEquals(savedUser.name(), deletedUser.name(), "Deleted user's name should match.");
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(savedUser.id()),
                "Deleting the same user again should throw an exception.");
    }

    @Test
    public void testSaveUserValidation() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.saveUser(null));
        assertEquals("Cannot save a null User", exception.getMessage(), "Exception message should match.");
    }
}
