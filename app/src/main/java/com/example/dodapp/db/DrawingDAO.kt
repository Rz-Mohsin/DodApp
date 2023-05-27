package com.example.dodapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dodapp.models.Drawing

@Dao
interface DrawingDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(drawing: Drawing): Long

    @Update
    suspend fun updateDrawing(drawing: Drawing)

    @Query("SELECT * FROM drawings")
    fun getAllDrawings(): LiveData<List<Drawing>>

    @Delete
    suspend fun deleteArticle(drawing : Drawing)

}