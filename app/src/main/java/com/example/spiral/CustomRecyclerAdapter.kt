package com.example.spiral

import android.content.Intent
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item.view.*


class CustomRecyclerAdapter(private val values: List<String>) :
    RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {

    private var patientDirPath = ""

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        itemView.cv.setOnClickListener{
            patientDirPath = Environment.getExternalStorageDirectory().absolutePath +
                    "/patients_new/" +
                    itemView.cv.textViewLarge.text

            val badHabitsIntent = Intent(parent.context, BadHabitsActivity::class.java)
            badHabitsIntent.putExtra("patientDirPath", patientDirPath)
            parent.context.startActivity(badHabitsIntent)
        }

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.largeTextView?.text = values[position]
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var largeTextView: TextView? = null

        init {
            largeTextView = itemView.findViewById(R.id.textViewLarge)
        }
    }
}