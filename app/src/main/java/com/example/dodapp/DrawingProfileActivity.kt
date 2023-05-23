package com.example.dodapp

import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import com.davemorrissey.labs.subscaleview.ImageSource
import com.example.dodapp.databinding.ActivityDrawingProfileBinding

class DrawingProfileActivity : AppCompatActivity(){

    private val TAG = "error001"

    private lateinit var binding : ActivityDrawingProfileBinding
    private lateinit var gestureDetector: GestureDetector
    private var pinID = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrawingProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.setImage(ImageSource.resource(R.drawable.ic_drawing_1))
        binding.imageView.isZoomEnabled = true
    }
}