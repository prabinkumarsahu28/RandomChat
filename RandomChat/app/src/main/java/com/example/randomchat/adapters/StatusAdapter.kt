package com.example.randomchat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.randomchat.R
import com.example.randomchat.models.UserStatus
import com.example.randomchat.viewholders.StatusViewHolder

class StatusAdapter(
    private val userStatusList: List<UserStatus>,
    private val supportFragmentManager: FragmentManager?
) :
    RecyclerView.Adapter<StatusViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.status_sample_layout, parent, false)

        return StatusViewHolder(view, supportFragmentManager)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val status = userStatusList[position]
        holder.setData(status)
    }

    override fun getItemCount(): Int {
        return userStatusList.size
    }
}