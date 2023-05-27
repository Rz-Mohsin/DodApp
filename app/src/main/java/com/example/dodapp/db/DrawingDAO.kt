package com.example.dodapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dodapp.models.Drawing

@Dao
interface DrawingDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(drawing: Drawing): Long

    @Query("SELECT * FROM drawings")
    fun getAllDrawings(): LiveData<List<Drawing>>

    @Delete
    suspend fun deleteArticle(drawing : Drawing)

}