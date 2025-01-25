package amt.services;

import amt.dto.DtoConverter;
import amt.dto.UserDTO;
import amt.entities.User;
import amt.repositories.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Service for managing {@link User} entities, including operations for creating,
 * retrieving, and deleting users. Converts between {@link User} entities and {@link UserDTO}.
 * This service interacts with the {@link UserRepository}.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@ApplicationScoped
public class UserService implements DtoConverter<User, UserDTO> {

    @Inject
    UserRepository userRepository;

    /**
     * Converts a {@link UserDTO} to a {@link User} entity.
     *
     * @param dto the DTO to convert
     * @return the corresponding {@link User} entity
     */
    @Override
    public User fromDTO(UserDTO dto) {
        User user = new User();
        user.setId(dto.id());
        user.setName(dto.name());
        return user;
    }

    /**
     * Converts a {@link User} entity to a {@link UserDTO}.
     *
     * @param user the entity to convert
     * @return the corresponding {@link UserDTO}
     */
    @Override
    public UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getCreatedAt());
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all {@link UserDTO}s
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    /**
     * Saves a new user or updates an existing one.
     *
     * @param user the user to save
     * @return the saved {@link UserDTO}
     * @throws IllegalArgumentException if the user is null
     */
    @Transactional
    public UserDTO saveUser(UserDTO user) {
        if (user == null) {
            throw new IllegalArgumentException("Cannot save a null User");
        }
        var userDto = toDTO(userRepository.save(fromDTO(user)));
        System.out.println("New user added : " + userDto.name());
        return userDto;
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @return the deleted {@link UserDTO}
     * @throws IllegalArgumentException if the user is not found
     */
    @Transactional
    public UserDTO deleteUser(Integer id) {
        var user = userRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id:" + id));
        userRepository.deleteById(id);
        System.out.println("User deleted : " + user.name() + " with id: " + user.id());
        return user;
    }
}
