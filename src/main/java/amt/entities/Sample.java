package amt.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="samples")
public class Sample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    private String name;

    @Column(nullable=false)
    private String path;

    @Column(nullable=false)
    private Long durationMs;

    @OneToMany
    private List<SampleTrack> sampleTracks;

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public List<SampleTrack> getSampleTracks() {
        return sampleTracks;
    }

    public void setSampleTracks(List<SampleTrack> sampleTracks) {
        this.sampleTracks = sampleTracks;
    }
}
