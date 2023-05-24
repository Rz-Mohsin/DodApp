package com.example.dodapp

import android.content.Context
import android.graphics.*
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.OnStateChangedListener
import java.util.ArrayList

class PinView (context: Context?, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr),OnStateChangedListener {
    private val sPin = ArrayList<PointF>()
    private val pinNames = ArrayList<String>()
    private var pin: Bitmap? = null
    private var gestureDetector: GestureDetector? = null
    private var pinClickListener: PinClickListener? = null
    private val clickBounds = ArrayList<RectF>()

    companion object {
        var pinID = 1
    }

    init {
        initialise()
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                Log.d("error001","double tap success")
                val point = viewToSourceCoord(PointF(e.x, e.y))
                point?.let {
                    val pinName = pinID++.toString()
                    setPin(it, pinName)
                    pinClickListener?.onPinClick(pinName)
                }
                invalidate()
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
            Log.d("error001","Single tap success")
            val x = event.x
            val y = event.y
            for (i in clickBounds.indices) {
                val bounds = clickBounds[i]
                if (bounds.contains(x, y)) {
                    pinClickListener?.onPinClick(pinNames[i])
                    performClick()
                    return true
                }
            }
        }
        return handled || super.onTouchEvent(event)
    }

    fun setPinClickListener(listener: PinClickListener) {
        this.pinClickListener = listener
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

        updateBounds()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

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

    override fun onScaleChanged(newScale: Float, origin: Int) {
        Log.d("error001","scale changed")
        updateBounds()
    }

    override fun onCenterChanged(newCenter: PointF?, origin: Int) {

    }

    private fun updateBounds(){
        clickBounds.clear()
        for (point in sPin) {
            if (point != null) {
                val vPin = sourceToViewCoord(point)
                val vX = vPin?.x ?: continue
                val vY = vPin.y

                val currentScale = scale

                val bounds = RectF(
                    vX - pin!!.width / 2 * currentScale,
                    vY - pin!!.height,
                    vX + pin!!.width / 2 * currentScale,
                    vY
                )
                clickBounds.add(bounds)
            }
        }
    }
}