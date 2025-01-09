package amt.repositories;

import amt.entities.User;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository extends BaseRepository<User, Long> {

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

    public boolean existsByName(String name) {
        String query = "SELECT COUNT(u) FROM User u WHERE u.name = :name";
        Long count = entityManager.createQuery(query, Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

}
