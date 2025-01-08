package amt.entities;


import jakarta.persistence.*;

@Entity
@Table(name="sample_tracks")
public class SampleTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne()
    private Sample sample;

    @OneToOne()
    private Track track;

    private long startTime;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
