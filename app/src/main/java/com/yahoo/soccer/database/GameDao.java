package com.yahoo.soccer.database;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yahoo.soccer.model.Game;

import java.util.List;

@Dao
public interface GameDao {

    @Query("SELECT * FROM GAME ORDER BY ID")
    List<Game> loadAllGames();

    @Query("SELECT EXISTS(SELECT * FROM GAME WHERE ID = :id)")
    boolean isRowIsExist(String id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertGame(Game game);

    @Update
    void updateGame(Game game);

    @Delete
    void delete(Game game);

    @Query("SELECT * FROM GAME WHERE id = :id")
    Game loadGameById(String id);
}
