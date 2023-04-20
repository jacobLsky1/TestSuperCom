package com.example.testsupercom.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testsupercom.R

class DeviceAdapter(val devices:List<String>): RecyclerView.Adapter<ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount() = devices.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var device = devices[position]
        holder.itemView.apply {
           var text = findViewById<TextView>(R.id.deviceName)
            text.text = device
        }
    }
}