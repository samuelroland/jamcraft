package amt.repositories;

import amt.entities.User;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

/**
 * Repository for managing {@link User} entities.
 * Extends the {@link BaseRepository} to inherit basic CRUD operations.
 * Provides additional methods specific to the {@link User} entity.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@ApplicationScoped
public class UserRepository extends BaseRepository<User, Integer> {

    public UserRepository() {
        super(User.class);
    }

}
