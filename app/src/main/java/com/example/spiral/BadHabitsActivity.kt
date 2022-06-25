package com.example.spiral

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_bad_habits.*


class BadHabitsActivity : AppCompatActivity() {

    var patientDirPath: String
    var badHabits: String

    init {
        patientDirPath = ""
        badHabits = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bad_habits)

        patientDirPath = intent.getStringExtra("patientDirPath").toString()

        checkBoxNone.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkBoxCoffee.isChecked = false
                checkBoxCoffee.isEnabled = false

                checkBoxEnergetics.isChecked = false
                checkBoxEnergetics.isEnabled = false

                checkBoxSmoke.isChecked = false
                checkBoxSmoke.isEnabled = false

                checkBoxAlcohol.isChecked = false
                checkBoxAlcohol.isEnabled = false

                checkBoxDrugs.isChecked = false
                checkBoxDrugs.isEnabled = false

                checkBoxPhysical.isChecked = false
                checkBoxPhysical.isEnabled = false
            } else {
                checkBoxCoffee.isEnabled = true
                checkBoxEnergetics.isEnabled = true
                checkBoxSmoke.isEnabled = true
                checkBoxAlcohol.isEnabled = true
                checkBoxDrugs.isEnabled = true
                checkBoxPhysical.isEnabled = true
            }
        }
    }

    override fun onBackPressed() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    fun collectContraindications(view: View) {
        badHabits = ""
        if (checkBoxCoffee.isChecked) badHabits += "C"
        if (checkBoxEnergetics.isChecked) badHabits += "E"
        if (checkBoxSmoke.isChecked) badHabits += "S"
        if (checkBoxAlcohol.isChecked) badHabits += "A"
        if (checkBoxDrugs.isChecked) badHabits += "D"
        if (checkBoxPhysical.isChecked) badHabits += "P"
        if (checkBoxNone.isChecked) badHabits += "N"

        if (badHabits.isEmpty()) {
            createMessage().show()
        } else {
            chooseHand().show()
        }
    }

    private fun createMessage(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ошибка")
                .setMessage("Внимательно прочтите и выберите хотя бы один пункт")
                .setPositiveButton("Хорошо") { dialog, id ->  dialog.cancel()
                }
        return builder.create()
    }

    private fun chooseHand(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Выберите руку")
            .setMessage("Выберите руку, которой будете рисовать")
            .setPositiveButton("Правая", myClickListener)
            .setNeutralButton("Левая", myClickListener)
        return builder.create()
    }

    var myClickListener: DialogInterface.OnClickListener = object : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface?, which: Int) {
            when (which) {
                Dialog.BUTTON_POSITIVE -> {
                    val spiralIntent = Intent(this@BadHabitsActivity, SpiralActivity::class.java)
                    spiralIntent.putExtra("hand", "R")
                    spiralIntent.putExtra("patientDirPath", patientDirPath)
                    spiralIntent.putExtra("badHabits", badHabits)
                    startActivity(spiralIntent)
                    finish()
                }
                Dialog.BUTTON_NEUTRAL -> {
                    val spiralIntent = Intent(this@BadHabitsActivity, SpiralActivity::class.java)
                    spiralIntent.putExtra("hand", "L")
                    spiralIntent.putExtra("patientDirPath", patientDirPath)
                    spiralIntent.putExtra("badHabits", badHabits)
                    startActivity(spiralIntent)
                    finish()
                }
            }
        }
    }
}