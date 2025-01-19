package amt.repositories;

import amt.entities.Sample;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class SampleRepository extends BaseRepository<Sample, Integer> {

    public SampleRepository() {
        super(Sample.class);
    }

    public List<Sample> searchByName(String name) {
        String query = "SELECT s FROM Sample s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))";
        return entityManager.createQuery(query, Sample.class)
                .setParameter("name", name)
                .getResultList();
    }
}
