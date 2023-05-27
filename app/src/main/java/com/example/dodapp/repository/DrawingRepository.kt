package com.example.dodapp.repository

import com.example.dodapp.db.DrawingDatabase
import com.example.dodapp.models.Drawing

class DrawingRepository(
    val db : DrawingDatabase
) {
    fun getAllDrawings() =
        db.getDrawingDao().getAllDrawings()

    suspend fun upsert(drawing : Drawing) {
        db.getDrawingDao().upsert(drawing)
    }

    suspend fun update(drawing: Drawing) {
        db.getDrawingDao().updateDrawing(drawing)
    }
}