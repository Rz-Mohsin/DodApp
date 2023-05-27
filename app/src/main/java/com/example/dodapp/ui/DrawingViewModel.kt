package com.example.dodapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.dodapp.models.Drawing
import com.example.dodapp.repository.DrawingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrawingViewModel(
    val repository : DrawingRepository
) : ViewModel() {

    fun getAllDrawing() = repository.getAllDrawings()

    fun saveDrawing(drawing: Drawing) =
        CoroutineScope(Dispatchers.IO).launch {
            repository.upsert(drawing)
        }

    fun updateDrawing(drawing: Drawing) =
        CoroutineScope(Dispatchers.IO).launch {
            repository.update(drawing)
        }
}