package com.example.randomchat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.randomchat.R
import com.example.randomchat.clicklisteners.UserClickListener
import com.example.randomchat.models.Users
import com.example.randomchat.viewholders.UserViewHolder

class UserAdapter(private val usersList: List<Users>, private val userClickListener: UserClickListener) :
    RecyclerView.Adapter<UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chats_sample_layout, parent, false)

        return UserViewHolder(view, userClickListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = usersList[position]
        holder.setData(user)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }
}