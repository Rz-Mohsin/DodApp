package com.example.dodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.davemorrissey.labs.subscaleview.ImageSource
import com.example.dodapp.databinding.ActivityDrawingProfileBinding
import com.example.dodapp.databinding.MarkerBottomSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class DrawingProfileActivity : AppCompatActivity(){

    private val TAG = "error001"

    private lateinit var binding : ActivityDrawingProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrawingProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.setImage(ImageSource.resource(R.drawable.ic_drawing_1))
        binding.imageView.isZoomEnabled = true
        binding.imageView.setPinClickListener(object : PinView.PinClickListener {
            override fun onPinClick(pinName: String) {
                // Handle the marker click event here
                Log.d("Pin Click", "Pin $pinName clicked")
                showBottomSheet()
            }
        })
    }

    private fun showBottomSheet(){
        val bottomSheetBinding = MarkerBottomSheetLayoutBinding.inflate(layoutInflater)

        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        val windowHeight = resources.displayMetrics.heightPixels
        val desiredHeight = (windowHeight * 0.7).toInt()
        val layoutParams = bottomSheetBinding.root.layoutParams
        layoutParams.height = desiredHeight
        bottomSheetBinding.root.layoutParams = layoutParams
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
    }
}