package com.example.forest.espbroadcast.Adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.forest.espbroadcast.R
import kotlinx.android.synthetic.main.davice_raw.view.*

class DeviceAdapter: RecyclerView.Adapter<CustomViewHolder>() {

    override fun getItemCount(): Int {
        return 20
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val callForRaw = layoutInflater.inflate(R.layout.davice_raw, parent, false)
        return CustomViewHolder(callForRaw)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.view.tv_ip.text = "192.168.0.101"
        holder.view.tv_name.text = "ESP Smart house"
    }

}

class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {

}

