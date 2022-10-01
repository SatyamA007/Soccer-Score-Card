package com.yahoo.soccer.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yahoo.soccer.model.Team;

import java.util.List;

@Dao
public interface TeamDao {

    @Query("SELECT EXISTS(SELECT * FROM TEAM WHERE ID = :id)")
    boolean isRowIsExist(String id);

    @Query("SELECT * FROM TEAM ORDER BY NAME")
    public List<Team> loadTeamByName();
    @Query("SELECT * FROM TEAM ORDER BY NAME DESC")
    public List<Team> loadTeamByNameRev();

    @Query("SELECT * FROM TEAM ORDER BY WIN DESC, NAME")
    public List<Team> loadTeamByWin();
    @Query("SELECT * FROM TEAM ORDER BY WIN ASC, NAME")
    public List<Team> loadTeamByWinRev();

    @Query("SELECT * FROM TEAM ORDER BY LOSS DESC, NAME")
    public List<Team> loadTeamByLoss();
    @Query("SELECT * FROM TEAM ORDER BY LOSS ASC, NAME")
    public List<Team> loadTeamByLossRev();

    @Query("SELECT * FROM TEAM ORDER BY DRAW DESC, NAME")
    public List<Team> loadTeamByDraw();
    @Query("SELECT * FROM TEAM ORDER BY DRAW ASC, NAME")
    public List<Team> loadTeamByDrawRev();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTeam(Team team);

    @Update
    void updateTeam(Team team);

    @Delete
    void delete(Team team);

    @Query("SELECT * FROM TEAM WHERE id = :id")
    Team loadTeamById(String id);
}
