package com.example.spiral

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private val uRL = "http://192.168.0.13:65400/api"
    var okHttpClient: OkHttpClient = OkHttpClient()
    var patientId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun createNewPatient(view: View) {
        val createPatientIntent = Intent(this, CreatePatientActivity::class.java)
        startActivity(createPatientIntent)
        finish()
    }

    fun viewPatientsList(view: View) {
        val patientsListIntent = Intent(this, ChoosePatient::class.java)
        startActivity(patientsListIntent)
        finish()
    }

    fun sendPatients(view: View) {
        sendNewPatient()
    }

    fun exit(view: View) {
        exitProcess(0)
    }


    private fun sendNewPatient() {
        val dirName = Environment.getExternalStorageDirectory().absolutePath + "/patients_new/"
        val files = File(dirName).listFiles()
        if (files !== null) {
            for (f in files) {
                if (f.isDirectory) {
                    val informationFile = File(f.absolutePath, "Информация.txt")
                    if (informationFile.exists()) {
                        parseInformationFileAndCreatePatientOrGetId(informationFile, f.listFiles())
                    } else {
                        println("В папке пациента " + f.absolutePath + " информационный файл отсутствует")
                        continue
                    }
                }
            }
        }
    }

    fun parseInformationFileAndCreatePatientOrGetId(informationFile: File, files: Array<File>) {
        val mapperAll = ObjectMapper()
        val jacksonObj = mapperAll.createObjectNode()
        var first_name = ""
        var second_name = ""
        var middle_name = ""
        var dob = ""

        for (line in informationFile.readLines()) {
            val lineParts = line.split(": ")
            when (lineParts[0]) {
                "Фамилия" -> {
                    second_name = lineParts[1]
                    jacksonObj.put("second_name", lineParts[1])
                }
                "Имя" -> {
                    first_name = lineParts[1]
                    jacksonObj.put("first_name", lineParts[1])
                }
                "Отчество" -> {
                    middle_name = lineParts[1]
                    jacksonObj.put("middle_name", lineParts[1])
                }
                "Дата рождения" -> {
                    val parsedDateObj = SimpleDateFormat("dd.MM.yyyy").parse(lineParts[1])
                    dob = SimpleDateFormat("yyyy-MM-dd").format(parsedDateObj)
                    jacksonObj.put("dob", dob)
                }
                "Диагноз" -> {
                    jacksonObj.put("diagnosis", lineParts[1])
                }
                "Пол" -> {
                    when (lineParts[1]) {
                        "Мужской" -> {
                            jacksonObj.put("sex", "male")
                        }
                        "Женский" -> {
                            jacksonObj.put("sex", "female")
                        }
                    }
                }
                "Доминирующая рука" -> {
                    when (lineParts[1]) {
                        "Правая" -> {
                            jacksonObj.put("dominant_hand", "R")
                        }
                        "Левая" -> {
                            jacksonObj.put("dominant_hand", "L")
                        }
                        "Обе" -> {
                            jacksonObj.put("dominant_hand", "B")
                        }
                    }
                }
            }
        }


        val urlBuilder: HttpUrl.Builder = "$uRL/patients".toHttpUrlOrNull()!!.newBuilder()
        urlBuilder.addQueryParameter("first_name", first_name)
        urlBuilder.addQueryParameter("second_name", second_name)
        urlBuilder.addQueryParameter("middle_name", middle_name)
        urlBuilder.addQueryParameter("dob", dob)

        val request = Request.Builder()
                .url(urlBuilder.build().toString())
                .build()

        jacksonObj.put("second_name", second_name)
        jacksonObj.put("first_name", first_name)
        jacksonObj.put("middle_name", middle_name)
//        val parsedDateObj = SimpleDateFormat("dd.MM.yyyy").parse(dob)
        jacksonObj.put("dob", dob)

        val jacksonString = jacksonObj.toString()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jacksonString.to(mediaType)



        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val myResponse = response.body?.string()
                        println("Найден пациент: $myResponse")

                        for (examFile in files) {
                            if ((examFile.isFile) and (examFile.name != "Информация.txt")) {
                                if (myResponse != null) {
                                    parseExamFileAndCreateExam(examFile, myResponse.toInt())
                                }
                            }
                        }

                    } else {
                        println("Error: ${response.code}")
                        if (response.code == 404) {
                            val jacksonString = jacksonObj.toString()
                            val mediaType = "application/json; charset=utf-8".toMediaType()
                            val body = jacksonString.toRequestBody(mediaType)
                            val url = "$uRL/patients"

                            val request = Request.Builder()
                                .url(url)
                                .addHeader("Accept", "application/json")
                                .post(body)
                                .build()

                            okHttpClient.newCall(request).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    e.printStackTrace()
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    response.use {
                                        if (response.isSuccessful) {
                                            println(response.body?.string())
                                        }
                                    }
                                }
                            })
                        }
                    }
                }
            }
        })
    }

    private fun parseExamFileAndCreateExam(examFile: File, id: Int) {
        val mapperAll = ObjectMapper()
        val jacksonObj = mapperAll.createObjectNode()

        val filenameParts = examFile.name.split("_", ".csv")
        println(filenameParts)

        jacksonObj.put("hand", filenameParts[0])
        jacksonObj.put("spiral_type", filenameParts[1])
        jacksonObj.put("bad_effects", filenameParts[2])
        val parsedDateObj = SimpleDateFormat("dd.MM.yyyy").parse(filenameParts[3])
        jacksonObj.put(
            "exam_date",
            SimpleDateFormat("yyyy-MM-dd").format(parsedDateObj)
        )
        jacksonObj.put("exam_time", filenameParts[4])
        jacksonObj.put("patient_id", id)


        val x: MutableList<Float> = mutableListOf()
        val y: MutableList<Float> = mutableListOf()
        val t: MutableList<Float> = mutableListOf()

        examFile.useLines { lines -> lines.forEach {
            if (it.isNotEmpty()) {
                val split = it.split(",")
                t.add(split[0].toFloat())
                x.add(split[1].toFloat())
                val result = split[2].substring(0, split[2].indexOf(';'))
                y.add(result.toFloat())
            }
        }}

        val jsonX = JSONArray()
        val jsonY = JSONArray()
        val jsonT = JSONArray()
        jsonX.put(x)
        jacksonObj.put("x", jsonX.toString())
        jsonY.put(y)
        jacksonObj.put("y", jsonY.toString())
        jsonT.put(t)
        jacksonObj.put("t", jsonT.toString())
//        println(json.toString())


        val jacksonString = jacksonObj.toString()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jacksonString.toRequestBody(mediaType)
        val url = "$uRL/exams"

        println("telo: $jacksonString")

        post(url, body)
    }

    private fun post(url: String, body: RequestBody) {
        val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .post(body)
                .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException(
                        "Unexpected code ${response.code}\n"
                                + response.body!!.string()
                    )
                }
            }
        })
    }
}