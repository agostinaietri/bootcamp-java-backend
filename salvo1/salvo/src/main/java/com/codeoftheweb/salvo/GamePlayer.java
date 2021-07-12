package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    private long id;
    private Date creationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;
    private Date joinDate;

    public GamePlayer() { }

    public GamePlayer(Game game, Player player, Date joinDate) {
        this.game = game;
        this.player = player;
        this.joinDate = new Date();
    }

    public Player getPlayer() {
        return player;
    }

    public Player setPlayer(Player player) {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
