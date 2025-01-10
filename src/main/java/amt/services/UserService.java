package amt.services;

import amt.dto.DtoConverter;
import amt.dto.UserDTO;
import amt.entities.User;
import amt.repositories.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class UserService implements DtoConverter<User, UserDTO> {

    @Inject
    UserRepository userRepository;

    @Override
    public User fromDTO(UserDTO dto) {
        User user = new User();
        user.setId(dto.id());
        user.setName(dto.name());
        return user;
    }

    @Override
    public UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getCreatedAt());
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id).map(this::toDTO).orElseThrow(() ->
                new IllegalArgumentException("User not found"));
    }

    @Transactional
    public UserDTO saveUser(UserDTO user) {
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return toDTO(userRepository.save(fromDTO(user)));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserDTO getUserByName(String name) {
        return userRepository.findByName(name).map(this::toDTO).orElseThrow(() ->
                new IllegalArgumentException("User not found"));
    }
}
