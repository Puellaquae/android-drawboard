package stkq.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawBoard(context: Context, attrs: AttributeSet) : View(context, attrs),
    View.OnTouchListener {
    data class BrushStyle(
        var red: Int,
        var green: Int,
        var blue: Int,
        var strokeWidth: Float,
        var forceTouch: Boolean = false,
        var pressureMeasured: Boolean = false,
        var pressureMin: Float = 0f,
        var pressureMax: Float = 1f,
    )

    private var paint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
        pathEffect = CornerPathEffect(160f)
    }
    private var draws: ArrayList<ArrayList<Triple<Path, BrushStyle, Float>>> = ArrayList()
    private var undoDraws: ArrayList<ArrayList<Triple<Path, BrushStyle, Float>>> = ArrayList()
    private var curPath: Path? = null

    var brush: BrushStyle = BrushStyle(0, 0, 0, 0.0f)

    init {
        setOnTouchListener(this)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (paths in draws) {
            for ((path, style, force) in paths) {
                paint.setARGB(255, style.red, style.green, style.blue)
                paint.strokeWidth = if (style.forceTouch) {
                    style.strokeWidth * force
                } else {
                    style.strokeWidth
                }
                canvas?.drawPath(path, paint)
            }
        }
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        val px = event?.x!!
        val py = event.y
        var force = event.pressure
        if (brush.pressureMeasured) {
            if (force > brush.pressureMax) {
                force = 1f
            } else if (force < brush.pressureMin) {
                force = 0f
            } else {
                force = (force - brush.pressureMin) / (brush.pressureMax - brush.pressureMin)
            }
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                undoDraws.clear()
                draws.add(ArrayList())
                curPath = Path()
                curPath!!.moveTo(px, py)
                draws.last().add(Triple(curPath!!, brush, force))
            }
            MotionEvent.ACTION_MOVE -> {
                if (brush.forceTouch && Math.abs(force - draws.last().last().third) > 0.01f) {
                    curPath?.lineTo(px, py)
                    curPath = Path()
                    curPath!!.moveTo(px, py)
                    draws.last().add(Triple(curPath!!, brush, force))
                } else {
                    curPath?.lineTo(px, py)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                curPath?.lineTo(px, py)
                invalidate()
            }
        }
        return true
    }

    fun undo() {
        if (draws.isNotEmpty()) {
            undoDraws.add(draws.last())
            draws.removeAt(draws.lastIndex)
            invalidate()
        }
    }

    fun redo() {
        if (undoDraws.isNotEmpty()) {
            draws.add(undoDraws.last())
            undoDraws.removeAt(undoDraws.lastIndex)
            invalidate()
        }
    }

    fun clear() {
        draws.clear()
        undoDraws.clear()
        invalidate()
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }
}