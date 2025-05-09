package com.example.talkTrack.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(
            mappedBy = "owner",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Presentation> presentations = new ArrayList<>();

    public void addPresentation(Presentation presentation) {
        presentations.add(presentation);
        presentation.setOwner(this);
    }

    public void removePresentation(Presentation presentation) {
        presentations.remove(presentation);
        presentation.setOwner(null);

    }
}
