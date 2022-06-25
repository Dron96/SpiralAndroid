package com.example.spiral

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_choose_patient.*
import java.io.File


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ChoosePatient : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_patient)

        patient_list.layoutManager = LinearLayoutManager(this)
        val patients = fillList()
        if (patients.isNotEmpty()) {
            textView.text = ""
            patient_list.adapter = CustomRecyclerAdapter(patients)
        } else {
            textView.text = "Пока не создано ни одного паицента"
        }
    }

    override fun onBackPressed() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    //TODO может нужно убрать это
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        finish()
    }

    private fun fillList(): List<String> {
        val data = mutableListOf<String>()
        val dirName = Environment.getExternalStorageDirectory().absolutePath + "/patients_new/"
        val files = File(dirName).listFiles()
        if (files !== null) {
            for (f in files) {
                if (f.isDirectory) data.add(f.name)
            }
        }

        return data
    }
}
