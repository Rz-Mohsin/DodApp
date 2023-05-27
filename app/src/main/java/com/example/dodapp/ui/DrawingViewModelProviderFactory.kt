package com.example.dodapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dodapp.repository.DrawingRepository

class DrawingViewModelProviderFactory(
    val repository : DrawingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DrawingViewModel(repository) as T
    }
}