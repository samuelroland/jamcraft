package amt.repositories;

import amt.entities.Sample;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Repository for managing {@link Sample} entities.
 * Extends the {@link BaseRepository} to inherit basic CRUD operations.
 * Provides additional methods specific to the {@link Sample} entity.
 *
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
@ApplicationScoped
public class SampleRepository extends BaseRepository<Sample, Integer> {

    public SampleRepository() {
        super(Sample.class);
    }

    /**
     * Searches for samples by name using a case-insensitive search.
     *
     * @param name The partial or full name to search for.
     * @return A list of samples whose names match the search term.
     */
    public List<Sample> searchByName(String name) {
        String query = "SELECT s FROM Sample s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))";
        return entityManager.createQuery(query, Sample.class)
                .setParameter("name", name)
                .getResultList();
    }
}
