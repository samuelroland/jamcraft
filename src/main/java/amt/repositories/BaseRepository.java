package amt.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

/**
 * A base repository class providing common CRUD operations for entities.
 * This class is designed to be extended by specific repositories for individual entities.
 *
 * @param <T>  The type of the entity managed by the repository.
 * @param <ID> The type of the entity's primary key.
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
public abstract class BaseRepository<T, ID> {

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> entityClass;

    /**
     * Constructs a new BaseRepository instance for the specified entity class.
     *
     * @param entityClass The class type of the entity managed by this repository.
     */
    public BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Saves a new entity to the database. If the entity already exists, it is updated (merged).
     *
     * @param entity The entity to save or update.
     * @return The saved or updated entity.
     */
    public T save(T entity) {
        if (entityManager.contains(entity)) {
            return entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
            return entity;
        }
    }

    /**
     * Finds an entity by its primary key.
     *
     * @param id The primary key of the entity to find.
     * @return An {@link Optional} containing the entity if found, or empty if not found.
     */
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    /**
     * Retrieves all entities of the specified type.
     *
     * @return A list of all entities managed by this repository.
     */
    public List<T> findAll() {
        String query = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        return entityManager.createQuery(query, entityClass).getResultList();
    }

    /**
     * Deletes an entity by its primary key.
     *
     * @param id The primary key of the entity to delete.
     */
    public void deleteById(ID id) {
        T entity = entityManager.find(entityClass, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
}
