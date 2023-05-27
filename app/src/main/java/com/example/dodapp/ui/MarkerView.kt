package com.example.dodapp.ui

import android.content.Context
import android.graphics.*
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.OnStateChangedListener
import com.example.dodapp.R
import java.util.ArrayList

class MarkerView (context: Context?, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr),OnStateChangedListener {
    private val sMarker = ArrayList<PointF>()
    private val markerIDs = ArrayList<String>()
    private var marker: Bitmap? = null
    private var gestureDetector: GestureDetector? = null
    private var markerClickListener: MarkerClickListener? = null
    val clickBounds = ArrayList<RectF>()

    init {
        Log.d("MarkerViewClass","Object of Marker View CLass created")
        initialise()
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
//                val point = viewToSourceCoord(PointF(e.x, e.y))
//                point?.let {
//                    val markerID = markerID++.toString()
//                    //setMarker(it, markerID)
//                    markerClickListener?.onDoubleTap(markerID, e.x, e.y)
//                    //store in database
//                }
//                invalidate()
                var id = 0
                if(markerIDs.isEmpty()){
                    id = 1
                } else {
                    id = markerIDs.last().toInt() + 1
                }
                markerClickListener?.onDoubleTap(id.toString(), e.x, e.y)
                return true
            }
        })
        setOnStateChangedListener(this)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val handled = gestureDetector?.onTouchEvent(event) ?: false
        if (!handled && event.action == MotionEvent.ACTION_DOWN) {
            Log.d("error001","Single tap success, size of click bounds : ${clickBounds.size}")
            val x = event.x
            val y = event.y
            for (i in clickBounds.indices) {
                val bounds = clickBounds[i]
                if (bounds.contains(x, y)) {
                    Log.d("error001","index in click bounds : $i")
                    markerClickListener?.onMarkerClick(markerIDs[i])
                    //fetch marker detail from database 
                    performClick()
                    return true
                }
            }
        }
        return handled || super.onTouchEvent(event)
    }

    fun setMarkerClickListener(listener: MarkerClickListener) {
        this.markerClickListener = listener
    }

    fun setMarker(sMarker: PointF, name: String): Boolean {
        return if (markerIDs.contains(name)) {
            false
        } else {
            this.sMarker.add(sMarker)
            markerIDs.add(name)
            initialise()
            invalidate()
            true
        }
    }

    fun getMarker(name: String): PointF {
        return sMarker[markerIDs.indexOf(name)]
    }

    fun removeMarker(name: String): Boolean {
        return if (markerIDs.contains(name)) {
            sMarker.removeAt(markerIDs.indexOf(name))
            markerIDs.remove(name)
            true
        } else {
            false
        }
    }

    private fun initialise() {
        val density = resources.displayMetrics.densityDpi.toFloat()
        val markerDrawable = ContextCompat.getDrawable(context, R.drawable.ic_marker_red)
        val markerBitmap = Bitmap.createBitmap(
            markerDrawable!!.intrinsicWidth,
            markerDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(markerBitmap)
        markerDrawable.setBounds(0, 0, canvas.width, canvas.height)
        markerDrawable.draw(canvas)

        markerBitmap?.let{
            val w = density / 420f * it.width
            val h = density / 420f * it.height
            marker = Bitmap.createScaledBitmap(it, w.toInt(), h.toInt(), true)
        }

        updateBounds()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isReady) {
            return
        }
        val paint = Paint()
        paint.isAntiAlias = true
        for (point in sMarker) {
            if (point != null && marker != null) {
                val vMarker = sourceToViewCoord(point)
                val vX = vMarker!!.x - marker!!.width / 2
                val vY = vMarker.y - marker!!.height
                canvas.drawBitmap(marker!!, vX, vY, paint)
            }
        }
    }

    interface MarkerClickListener {
        fun onMarkerClick(markerID: String)
        fun onDoubleTap(markerID : String, x : Float, y : Float)
    }

    override fun onScaleChanged(newScale: Float, origin: Int) {
        Log.d("error001","scale changed")
        updateBounds()
    }

    override fun onCenterChanged(newCenter: PointF?, origin: Int) {

    }

    private fun updateBounds(){
        clickBounds.clear()
        for (point in sMarker) {
            if (point != null) {
                val vMarker = sourceToViewCoord(point)
                val vX = vMarker?.x ?: continue
                val vY = vMarker.y

                val currentScale = scale

                val bounds = RectF(
                    vX - marker!!.width / 2 * currentScale,
                    vY - marker!!.height,
                    vX + marker!!.width / 2 * currentScale,
                    vY
                )
                clickBounds.add(bounds)
            }
        }
    }
}