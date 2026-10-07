package io.github.kirillbadin.tennisscoreboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Match {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player1", referencedColumnName = "id", nullable = false)
    private Player firstPlayer;

    @ManyToOne
    @JoinColumn(name = "player2", referencedColumnName = "id", nullable = false)
    private Player secondPlayer;

    @ManyToOne
    @JoinColumn(name = "winner", referencedColumnName = "id", nullable = false)
    private Player winner;

    public Match(Player firstPlayer, Player secondPlayer, Player winner) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        if(!winner.equals(firstPlayer)&& !winner.equals(secondPlayer)){
                throw new IllegalArgumentException("Победитель не участник матча");
        }
        this.winner = winner;
    }
}