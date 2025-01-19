package amt.entities;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sample_tracks")
public class SampleTrack implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @ManyToOne
    @JoinColumn(name = "sample_id", nullable = false)
    private Sample sample;

    @Column(name = "start_time", nullable = false)
    private Double startTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Double getStartTime() {
        return startTime;
    }

    public void setStartTime(Double startTime) {
        this.startTime = startTime;
    }
}
