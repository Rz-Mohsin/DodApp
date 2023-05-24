package com.example.dodapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dodapp.databinding.ActivityDrawingListBinding

class DrawingListActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDrawingListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddDrawing.setOnClickListener {
            val intent = Intent(this,DrawingProfileActivity::class.java)
            startActivity(intent)
        }
    }
}