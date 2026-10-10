package io.github.kirillbadin.tennisscoreboard.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "matches", check = {
        @CheckConstraint(name = "chk_different_players", constraint = "first_player_id <> second_player_id"),
        @CheckConstraint(name = "chk_winner_one_of_players", constraint = "winner_id = first_player_id OR winner_id = second_player_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Match {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "first_player_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Player firstPlayer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "second_player_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Player secondPlayer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "winner_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Player winner;

    public Match(Player firstPlayer, Player secondPlayer, Player winner) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.winner = winner;
    }
}