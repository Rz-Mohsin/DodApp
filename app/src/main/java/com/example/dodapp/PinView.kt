package com.example.dodapp

import android.content.Context
import android.graphics.*
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import java.util.ArrayList

class PinView (context: Context?, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr) {
    private val sPin = ArrayList<PointF>()
    private val pinNames = ArrayList<String>()
    private var pin: Bitmap? = null
    private var gestureDetector: GestureDetector? = null
    private var pinClickListener: PinClickListener? = null

    companion object {
        var pinID = 1
    }

    init {
        initialise()
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                Log.d("error001","double tap success")
                val point = viewToSourceCoord(PointF(e.x,e.y))
                point?.let {
                    setPin(it, pinID++.toString())
                }
                invalidate()
                return true
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val handled = gestureDetector?.onTouchEvent(event) ?: false
        return handled || super.onTouchEvent(event)
    }

    fun setPin(sPin: PointF, name: String): Boolean {
        return if (pinNames.contains(name)) {
            false
        } else {
            this.sPin.add(sPin)
            pinNames.add(name)
            initialise()
            invalidate()
            true
        }
    }

    fun getPin(name: String): PointF {
        return sPin[pinNames.indexOf(name)]
    }

    fun removePin(name: String): Boolean {
        return if (pinNames.contains(name)) {
            sPin.removeAt(pinNames.indexOf(name))
            pinNames.remove(name)
            true
        } else {
            false
        }
    }

    private fun initialise() {
        val density = resources.displayMetrics.densityDpi.toFloat()
        val markerDrawable = ContextCompat.getDrawable(context, R.drawable.ic_marker)
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
            pin = Bitmap.createScaledBitmap(it, w.toInt(), h.toInt(), true)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady) {
            return
        }
        val paint = Paint()
        paint.isAntiAlias = true
        for (point in sPin) {
            if (point != null && pin != null) {
                val vPin = sourceToViewCoord(point)
                val vX = vPin!!.x - pin!!.width / 2
                val vY = vPin.y - pin!!.height
                canvas.drawBitmap(pin!!, vX, vY, paint)
            }
        }
    }

    interface PinClickListener {
        fun onPinClick(pinName: String)
    }
}