package com.example.spiral

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_patient.*
import java.io.File
import java.io.FileNotFoundException

class CreatePatientActivity : AppCompatActivity() {

    var patientDirPath = ""

    init {
        patientDirPath = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_patient)
        DateInputMask(dateOfBirth)
    }

    override fun onBackPressed() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    fun createPatientFolder(view: View) {
        val error = "Поле обязательно для заполнения"
        var allOk = true
        // Проверка, что поля с ФИО и датой рождения заполнены и считывание данных из полей
        if (secondName.text.toString() == "") {
            secondName.error = error
            allOk = false
        }
        if (firstName.text.toString() == "") {
            firstName.error = error
            allOk = false
        }
        if (middleName.text.toString() == "") {
            middleName.error = error
            allOk = false
        }
        if (dateOfBirth.text.toString() == "") {
            dateOfBirth.error = error
            allOk = false
        }
        // Если поля с ФИО и датой рождения заполнены, то идем далее
        if (allOk) {secondName.text.toString()
            val folderName = secondName.text.toString() +
                        " " + firstName.text.toString() +
                        " " + middleName.text.toString() +
                        " " + dateOfBirth.text.toString()
            // Считывание данных с чекбоксов и выпадающего списка с диагнозом
            var selectedRadioButton = findViewById<RadioButton>(dominant_hand.checkedRadioButtonId)
            val dominantHandValue = selectedRadioButton.text.toString()
            selectedRadioButton = findViewById(sex.checkedRadioButtonId)
            val sexValue = selectedRadioButton.text.toString()
            val diagnosisValue = diagnosis.selectedItem.toString()
            // Создание директории, файла с информацией о пациенте и запись в него информации
            patientDirPath = Environment.getExternalStorageDirectory().absolutePath + "/patients_new/" + folderName
            val patientsDirectory = File(patientDirPath)
            patientsDirectory.mkdirs()
            val outputFile = File(patientsDirectory, "Информация.txt")
            if (outputFile.exists()) {
                createMessage().show()
            } else {
                try {
                    outputFile.appendText("Фамилия: " + secondName.text.toString() + "\n")
                    outputFile.appendText("Имя: " + firstName.text.toString() + "\n")
                    outputFile.appendText("Отчество: " + middleName.text.toString() + "\n")
                    outputFile.appendText("Дата рождения: " + dateOfBirth.text.toString() + "\n")
                    outputFile.appendText("Диагноз: $diagnosisValue\n")
                    outputFile.appendText("Пол: $sexValue\n")
                    outputFile.appendText("Доминирующая рука: $dominantHandValue\n")
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                goToExamination().show()
            }
        }
    }

    private fun createMessage(): AlertDialog {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Ошибка")
                    .setMessage("Папка с таким пациентом уже существует")
                    .setPositiveButton("Хорошо") {
                        dialog, id ->  dialog.cancel()
                    }
            return builder.create()
    }

    private fun goToExamination(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Приступить к исследованию?")
                .setPositiveButton("Да", myClickListener)
                .setNeutralButton("Нет", myClickListener)
        return builder.create()
    }

    var myClickListener: DialogInterface.OnClickListener = object : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface?, which: Int) {
            when (which) {
                Dialog.BUTTON_POSITIVE -> {
                    val badHabitsIntent = Intent(baseContext, BadHabitsActivity::class.java)
                    badHabitsIntent.putExtra("patientDirPath", patientDirPath)
                    startActivity(badHabitsIntent)
                    finish()
                }
                Dialog.BUTTON_NEUTRAL -> {
                    // Переход к главному экрану
                    val mainActivityIntent = Intent(baseContext, MainActivity::class.java)
                    startActivity(mainActivityIntent)
                    finish()
                }
            }
        }
    }
}