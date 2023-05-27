package com.example.dodapp.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dodapp.R
import com.example.dodapp.databinding.ActivityDrawingListBinding
import com.example.dodapp.db.DrawingDatabase
import com.example.dodapp.repository.DrawingRepository
import com.example.dodapp.ui.DrawingViewModel
import com.example.dodapp.ui.DrawingViewModelProviderFactory

class DrawingListActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDrawingListBinding
    lateinit var viewmodel : DrawingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = DrawingRepository(DrawingDatabase(this))
        viewmodel = ViewModelProvider(this,DrawingViewModelProviderFactory(repository))[DrawingViewModel::class.java]

        binding.btnAddDrawing.setOnClickListener {
            val fragment = DrawingProfileFragment()
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.addToBackStack("Profile Fragment")
            transaction.add(R.id.fragmentContainer,fragment,null)
            transaction.commit()
        }
        getAllDrawings()
    }

    private fun getAllDrawings() {
        viewmodel.getAllDrawing().observe(this, Observer { drawings ->
            Log.d("error001","Size of drawing list : ${drawings.size}")
        })
    }
}