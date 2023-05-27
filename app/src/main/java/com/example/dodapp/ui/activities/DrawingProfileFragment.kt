package com.example.dodapp.ui.activities

import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.example.dodapp.databinding.FragmentDrawingProfileBinding
import com.example.dodapp.databinding.MarkerBottomSheetLayoutBinding
import com.example.dodapp.models.Drawing
import com.example.dodapp.models.Marker
import com.example.dodapp.ui.DrawingViewModel
import com.example.dodapp.ui.MarkerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class DrawingProfileFragment : Fragment(){

    private lateinit var binding : FragmentDrawingProfileBinding
    private lateinit var viewModel: DrawingViewModel
    lateinit var bottomSheetBinding : MarkerBottomSheetLayoutBinding
    private var chosenDrawing : String? = null
    private val markersList = ArrayList<Marker>()

    private val imageChooserLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri: Uri? ->
        imageUri?.let {
            binding.imageView.setImage(ImageSource.uri(it))
            chosenDrawing = it.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDrawingProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as DrawingListActivity).viewmodel

        binding.imageView.isZoomEnabled = true
        binding.imageView.setMarkerClickListener(object : MarkerView.MarkerClickListener {
            override fun onMarkerClick(markerID: String) {
                Log.d("Marker Click", "Marker $markerID clicked")
                //showMarkerDetail() from database 
            }

            override fun onDoubleTap(markerID : String, x : Float, y : Float) {
                showBottomSheet(markerID,x,y)
            }
        })

        binding.btnChooseDrawing.setOnClickListener {
            imageChooserLauncher.launch("image/*")
        }

        binding.btnSaveDrawing.setOnClickListener {
            val drawingName = binding.etDrawingName.toString()
            if (chosenDrawing.isNullOrEmpty()){
                Toast.makeText(requireContext(),"Failed to save drawing", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.saveDrawing(
                    Drawing(
                        null,
                        drawingName,
                        System.currentTimeMillis(),
                        chosenDrawing!!,
                        markersList
                    )
                )
                Toast.makeText(requireContext(),"Drawing Saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showBottomSheet(markerID : String, x : Float, y : Float){
        bottomSheetBinding = MarkerBottomSheetLayoutBinding.inflate(layoutInflater)

        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.btnSave.setOnClickListener {
            binding.apply {
                val point = imageView.viewToSourceCoord(PointF(x, y))
                point?.let {
                    imageView.setMarker(it, markerID)
                    saveMarker(markerID,x,y)
                }
                imageView.invalidate()
            }
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

    private fun saveMarker(markerID : String, x : Float, y : Float) {
        val markerTitle = bottomSheetBinding.tvMarkerTitle.toString()
        val markerDes = bottomSheetBinding.tvMarkerDes.toString()
        if(markerTitle.isNotEmpty() && markerDes.isNotEmpty()){
            markersList.add(Marker(
                markerID,
                x,
                y,
                markerTitle,
                markerDes,
                System.currentTimeMillis()
                ))

        } else {
            Toast.makeText(requireContext(),"Marker title and description can't be empty!",Toast.LENGTH_SHORT).show()
        }

    }
}