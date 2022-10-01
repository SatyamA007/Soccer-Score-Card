package com.yahoo.soccer.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "game")
public class Game {
    @NonNull
    @PrimaryKey
    String id;
    String aname;
    String bname;
    String ascore;
    String bscore;

    @Ignore
    public Game(String aname, String bname, String ascore, String bscore) {
        this.aname = aname;
        this.bname = bname;
        this.ascore = ascore;
        this.bscore = bscore;
        id = "0";
    }

    public Game(String id, String aname, String bname, String ascore, String bscore) {
        this.id = id;
        this.aname = aname;
        this.bname = bname;
        this.ascore = ascore;
        this.bscore = bscore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
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
