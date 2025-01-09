package amt.repositories;

import amt.entities.Sample;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SampleRepository extends BaseRepository<Sample, Long> {

    public SampleRepository() {
        super(Sample.class);
    }

    public List<Sample> searchByName(String name){
      // TODO
        List<Sample> list = new ArrayList<>();
        list.add(new Sample());
        return list;
    }
}
