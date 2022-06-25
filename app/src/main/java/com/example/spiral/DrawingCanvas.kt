package com.example.spiral

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.io.File

class DrawingCanvas(context: Context?, attrs: AttributeSet? = null) : View(context, attrs) {
    private var startTime: Long
    private val mPaint: Paint = Paint()
    private val mPath: Path
    var filePath: String = ""

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(mPath, mPaint)
        super.onDraw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        filePath = tag as String
        val outputFile = File(filePath)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {startTime
                mPath.moveTo(event.x, event.y)
                val time = ((System.currentTimeMillis() - startTime).toFloat() / 1000)
                outputFile.appendText(time.toString() + "," + event.x.toString() + "," + event.y.toString() + ";\n")
            }
            MotionEvent.ACTION_MOVE -> {
                mPath.lineTo(event.x, event.y)
                val time = ((System.currentTimeMillis() - startTime).toFloat() / 1000)
                outputFile.appendText(time.toString() + "," + event.x.toString() + "," + event.y.toString() + ";\n")
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                outputFile.appendText("\n\n")
            }
        }
        return true
    }

    init {
        mPaint.color = Color.GREEN
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = 5f
        mPath = Path()
        startTime = System.currentTimeMillis()
    }
}