package com.example.dodapp.ui.activities

import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.example.dodapp.R
import com.example.dodapp.databinding.FragmentDrawingDetailsBinding
import com.example.dodapp.databinding.LayoutMarkerDetailsBinding
import com.example.dodapp.databinding.MarkerBottomSheetLayoutBinding
import com.example.dodapp.models.Drawing
import com.example.dodapp.models.Marker
import com.example.dodapp.ui.DrawingViewModel
import com.example.dodapp.ui.MarkerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class DrawingDetailsFragment : Fragment() {

    private lateinit var viewModel: DrawingViewModel
    private lateinit var binding : FragmentDrawingDetailsBinding
    private lateinit var bottomSheetBinding : MarkerBottomSheetLayoutBinding
    private lateinit var addMarkerBinding : LayoutMarkerDetailsBinding
    private var drawing : Drawing? = null
    private var markersList = ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(arguments!=null){
            drawing = requireArguments().getSerializable("drawing") as Drawing?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDrawingDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawing?.let { _drawing ->
            setDrawing(_drawing)
        }
        viewModel = (activity as DrawingListActivity).viewmodel

        binding.ivDrawing.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                binding.ivDrawing.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Post a runnable to execute after the view is laid out
                binding.ivDrawing.post {
                    for(marker in markersList) {
                        val point = binding.ivDrawing.viewToSourceCoord(PointF(marker.positionX, marker.positionY))
                        Log.d("error001","point is : $point")
                        point?.let {
                            Log.d("error001","Setting the marker")
                            binding.ivDrawing.setMarker(it, marker.markerID)
                        }
                    }
                    setMarkerClickListener()
                }
            }
        })

        binding.btnUpdateDrawing.setOnClickListener{
            drawing?.let { _drawing ->
                _drawing.markers = markersList
                viewModel.updateDrawing(
                    _drawing
                )
                Toast.makeText(requireContext(),"Drawing updated!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setDrawing(_drawing : Drawing) {
        markersList = _drawing.markers as ArrayList<Marker>
        binding.apply {
            Log.d("error001","Number of Markers : ${markersList[0]}")
            val imageBitmap = BitmapFactory.decodeByteArray(_drawing.imageBytes, 0, _drawing.imageBytes.size)
            ivDrawing.setImage(ImageSource.bitmap(imageBitmap))
            tvDrawingName.text = _drawing.name
        }
    }

    private fun setMarkerClickListener() {
        binding.ivDrawing.setMarkerClickListener(object : MarkerView.MarkerClickListener {
            override fun onMarkerClick(markerID: String) {
                Log.d("Marker Click", "Marker $markerID clicked")
                if(markerID <= binding.ivDrawing.clickBounds.size.toString()) {
                    showMarkerDetails(markerID)
                }
            }

            override fun onDoubleTap(markerID : String, x : Float, y : Float) {
                showBottomSheet(markerID,x,y)
            }
        })
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
                val point = ivDrawing.viewToSourceCoord(PointF(x, y))
                point?.let {
                    ivDrawing.setMarker(it, markerID)
                    saveMarker(markerID,x,y)
                    ivDrawing.invalidate()
                }
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
        val markerTitle = bottomSheetBinding.tvMarkerTitle.text.toString()
        val markerDes = bottomSheetBinding.tvMarkerDes.text.toString()
        if(markerTitle.isNotEmpty() && markerDes.isNotEmpty()){
            markersList.add(Marker(
                markerID,
                x,
                y,
                markerTitle,
                markerDes,
                System.currentTimeMillis()
            ))
            Toast.makeText(requireContext(),"Marker added!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(),"Marker title and description can't be empty!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showMarkerDetails(markerID : String){
        val foundMarker: Marker? = markersList.find { marker ->
            marker.markerID == markerID
        }
        if(foundMarker!=null){
            showMarkerDetailsDialog(foundMarker)
        } else {
            Toast.makeText(requireContext(),"Choose a marker",Toast.LENGTH_SHORT).show()
        }
    }

    private fun showMarkerDetailsDialog(marker : Marker) {

        addMarkerBinding = LayoutMarkerDetailsBinding.inflate(layoutInflater)

        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomSheetDialog.setContentView(addMarkerBinding.root)

        addMarkerBinding.apply {
            btnClose.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            tvMarkerTitle.text = marker.title
            tvMarkerDes.text = marker.description
        }

        val windowHeight = resources.displayMetrics.heightPixels
        val desiredHeight = (windowHeight * 0.5).toInt()
        val layoutParams = addMarkerBinding.root.layoutParams
        layoutParams.height = desiredHeight
        addMarkerBinding.root.layoutParams = layoutParams
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.show()
    }

}