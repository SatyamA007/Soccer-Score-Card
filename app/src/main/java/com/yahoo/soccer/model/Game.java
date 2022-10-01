package com.yahoo.soccer.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "game")
public class Game {
    @NonNull
    @PrimaryKey
    String id;
    String aid;
    String bid;
    String ascore;
    String bscore;

    public Game(String id, String aid, String bid, String ascore, String bscore) {
        this.id = id;
        this.aid = aid;
        this.bid = bid;
        this.ascore = ascore;
        this.bscore = bscore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getAscore() {
        return ascore;
    }

    public void setAscore(String ascore) {
        this.ascore = ascore;
    }

    public String getBscore() {
        return bscore;
    }

    public void setBscore(String bscore) {
        this.bscore = bscore;
    }
}
