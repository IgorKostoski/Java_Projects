package com.example.talkTrack.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "presentations")
@Data@NoArgsConstructor
@AllArgsConstructor
public class Presentation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String uniqueCode;

    @Enumerated(EnumType.STRING)
    private PresentationStatus status = PresentationStatus.DRAFT;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(
            mappedBy = "presentation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )

    @OrderBy("slideOrder ASC")
    private List<Slide> slides = new ArrayList<>();

    private Integer currentSlideIndex = 0;

    public void addSlide(Slide slide) {
        slides.add(slide);
        slides.setPresentation(this);
    }

    public void removeSlide(Slide slide) {
        slides.remove(slide);
        slide.setPresentation(null);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
