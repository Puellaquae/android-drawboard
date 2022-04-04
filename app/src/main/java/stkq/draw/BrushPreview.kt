package stkq.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BrushPreview(context: Context, attrs: AttributeSet) : View(context, attrs) {
    var red: Int = 0
        set(value) {
            field = value
            invalidate()
        }
        get
    var green: Int = 0
        set(value) {
            field = value
            invalidate()
        }
        get
    var blue: Int = 0
        set(value) {
            field = value
            invalidate()
        }
        get
    var width: Float = 0.0f
        set(value) {
            field = value
            invalidate()
        }
        get
    var touch: Boolean = false
    val brushStyle get() = DrawBoard.BrushStyle(red, green, blue, width, touch)
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
}