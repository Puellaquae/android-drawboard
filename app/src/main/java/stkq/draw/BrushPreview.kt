package stkq.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class BrushPreview(context: Context, attrs: AttributeSet) : View(context, attrs),
    View.OnTouchListener {
    var red: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var green: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var blue: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var width: Float = 0.0f
        set(value) {
            field = value
            invalidate()
        }
    var touch: Boolean = false
    var pressureMeasured = false
    var pressureMin = 0f
    var pressureMax = 1f
    private var pressureSupport = false
    var brushStyle
        get() = DrawBoard.BrushStyle(red, green, blue, width, touch, pressureMeasured)
        set(value) {
            red = value.red
            green = value.green
            blue = value.blue
            width = value.strokeWidth
            touch = value.forceTouch
            pressureMeasured = value.pressureMeasured
            pressureMin = value.pressureMin
            pressureMax = value.pressureMax
            invalidate()
        }
    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val w = measuredWidth
        val h = measuredHeight
        paint.apply {
            setARGB(255, red, green, blue)
            strokeWidth = width
        }
        val cx = w / 2.0f
        val quarterW = w / 4.0f
        val cy = h / 2.0f
        canvas?.drawLine(cx - quarterW, cy, cx + quarterW, cy, paint)
    }

    init {
        setOnTouchListener(this)
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        if (!brushStyle.forceTouch) {
            return true
        }
        val force = event!!.pressure
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressureMax = 1f
                pressureMin = 0f
            }
            MotionEvent.ACTION_MOVE -> {
                pressureMax = maxOf(pressureMax, force)
                pressureMin = minOf(pressureMin, force)
                if (0f < force && force < 1f) {
                    pressureSupport = true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!pressureSupport) {
                    Toast.makeText(context, R.string.pressure_nonsupport, Toast.LENGTH_LONG).show()
                } else {
                    pressureMeasured = true
                    Toast.makeText(context, R.string.pressure_fin, Toast.LENGTH_LONG).show()
                }
            }
        }
        return true
    }
}