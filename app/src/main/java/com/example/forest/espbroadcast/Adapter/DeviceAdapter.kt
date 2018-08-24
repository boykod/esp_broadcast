package com.example.forest.espbroadcast.Adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.forest.espbroadcast.JsonDataModel
import com.example.forest.espbroadcast.R

class DeviceAdapter(jsonData: ArrayList<JsonDataModel>): RecyclerView.Adapter<CustomViewHolder>() {

    private var mJsonData: ArrayList<JsonDataModel> = jsonData

    override fun getItemCount(): Int {
        return if (!mJsonData.isEmpty()) mJsonData.size else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.davice_raw, parent,false))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(mJsonData[position])
    }

}

class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {

    private val tvName : TextView by lazy { view.findViewById<TextView>(R.id.tv_name) }
    private val tvIp : TextView by lazy { view.findViewById<TextView>(R.id.tv_ip) }

    fun bind (data: JsonDataModel) {
        tvName.text = data.data.name
        tvIp.text = data.data.ip
    }
}

