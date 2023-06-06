package com.wabs.server.database.entites;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Getter
@Setter
@Table(name = "players_statistics")
public class PlayerStatistics {

    public PlayerStatistics() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @OneToOne(mappedBy = "playerStatistics")
    private DbPlayer playerId;

    @Column(name = "total_games")
    private int totalGames;

    @Column(name = "victories")
    private int victories;

    @Column(name = "defeats")
    private int defeats;

    @Column(name = "kd")
    private BigDecimal kd;

    @Column(name = "kills")
    private int kills;

    @Column(name = "damage_dealt")
    private int damageDealt;

    @Column(name = "deaths")
    private int deaths;

    @Column(name = "damage_received")
    private int damageReceived;

    public void addGame(boolean win) {
        totalGames++;
        if (win) {
            victories++;
        } else {
            defeats++;
        }
    }

    public void addKills(int kills) {
        this.kills += kills;
    }

    public void addDamageDealt(int damage) {
        this.damageDealt += damage;
    }

    public void addDamageReceived(int damage) {
        this.damageReceived += damage;
    }

    public void addDeaths(int deaths) {
        this.deaths += deaths;
    }

    public void refreshKd() {
        if (deaths == 0) {
            kd = new BigDecimal(kills).divide(new BigDecimal(1), 2, RoundingMode.HALF_UP);
        } else {
            kd = new BigDecimal(kills).divide(new BigDecimal(deaths), 2, RoundingMode.HALF_UP);
        }
    }

}
