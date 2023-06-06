package com.wabs.server.database.entites;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "matches")
public class Match {

    public Match() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "win")
    private boolean win;

    @Column(name = "date")
    private LocalDateTime date;

    @OneToMany(mappedBy = "match")
    private List<PlayerMatchStatistics> playerMatchStats;

    @PrePersist
    public void prePersist() {
        date = LocalDateTime.now();
    }

}
