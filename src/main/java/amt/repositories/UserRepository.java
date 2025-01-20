package amt.repositories;

import amt.entities.User;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository extends BaseRepository<User, Integer> {

    public UserRepository() {
        super(User.class);
    }

    public Optional<User> findByName(String name) {
        String query = "SELECT u FROM User u WHERE u.name = :name";
        return entityManager.createQuery(query, User.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }
}
