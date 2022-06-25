package com.example.spiral

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.os.Environment
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_in_spiral.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class InSpiralActivity : AppCompatActivity() {

    private var patientDirPath: String = ""
    private var hand: String = ""
    private var badHabits: String = ""

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_spiral)

        patientDirPath = intent.getStringExtra("patientDirPath").toString()
        hand = intent.getStringExtra("hand").toString()
        badHabits = intent.getStringExtra("badHabits").toString()

        var handString = ""
        when (hand) {
            "L" -> handString = "Левая"
            "R" -> handString = "Правая"
        }
        title = "$title $handString рука"

        val sdf = SimpleDateFormat("dd.MM.yyyy_HH:mm:ss")
        val currentDate = sdf.format(Date())
        val filename = hand + "_In_" + badHabits + "_" + currentDate.toString() + ".csv"
        Toast.makeText(this, filename, Toast.LENGTH_LONG).show()

        picture.tag = "$patientDirPath/$filename"
    }

    fun nextSpiral(view: View) {
        val nextIntent = Intent(this, CopySpiralActivity::class.java)
        nextIntent.putExtra("hand", hand)
        nextIntent.putExtra("patientDirPath", patientDirPath)
        nextIntent.putExtra("badHabits", badHabits)
        startActivity(nextIntent)
        finish()
    }

    override fun onBackPressed() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    internal class InSpiralView(context: Context?, attrs: AttributeSet) : View(context) {

        private var paint: Paint = Paint()
        private var path: Path

        init {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 5f
            paint.strokeCap = Paint.Cap.ROUND
            paint.color = Color.RED
            path = Path()
        }

        @SuppressLint("DrawAllocation")
        override fun onDraw(canvas: Canvas) {
            val dirName = Environment.getExternalStorageDirectory().absolutePath + "/patients_new/"
            val outputFile = File(dirName, "spiral.txt")
            if (!outputFile.exists()) {
                generateSpiralFile(outputFile)
            }
            path.reset()
            path.moveTo((width / 2).toFloat(), (height / 2).toFloat())
            var i = 1
            for (line in outputFile.readLines()) {
                val point = line.split(",").map { it.toFloat() }
                i++
                path.lineTo(point[0], point[1])
            }
            // выводим результат
            canvas.drawPath(path, paint)
        }

        private fun generateSpiralFile(outputFile: File) {
            val g = 0.1
            for (i in 1..3400) {
                val t = i * g
                val x = (t * sin(t / 18)).toFloat()
                val y = (t * cos(t / 18)).toFloat()
                outputFile.appendText((width / 2 + x).toString() + ',' + (height / 2 - y).toString() + "\n")
            }
        }
    }
}
