package io.github.kirillbadin.tennisscoreboard.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name ="players", indexes = @Index(name = "idx_player_name", columnList = "name", unique = true))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, updatable = false, length = 50)
    private String name;

    public Player(String name) {
        this.name = name;
    }
}