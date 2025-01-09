package amt.repositories;

import amt.entities.User;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository extends BaseRepository<User, Long> {

    public UserRepository() {
        super(User.class);
    }

    public Optional<User> findByName(String name){
       // TODO

        var user = new User();
        return Optional.of(user);
    }
}
