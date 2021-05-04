package com.example.randomchat.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.randomchat.R
import com.example.randomchat.clicklisteners.UserClickListener
import com.example.randomchat.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.chats_sample_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class UserViewHolder(view: View, private val userClickListener: UserClickListener) :
    RecyclerView.ViewHolder(view) {

    fun setData(user: Users) {

        itemView.apply {

            Glide.with(this).load(user.dp).placeholder(R.drawable.ic_user).into(profilePicChats)
            tvUserNameChats.text = user.userName

            FirebaseDatabase.getInstance()
                .reference.child("Chats")
                .child(FirebaseAuth.getInstance().uid + user.userId)
                .orderByChild("timestamp")
                .limitToLast(1)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChildren()) {
                            for (dataSnapshot in snapshot.children) {
                                tvLastMessageChats.text =
                                    dataSnapshot.child("message").value.toString()

                                val time =
                                    Date(dataSnapshot.child("timeStamp").value.toString().toLong())
                                val pre = SimpleDateFormat("KK:mm a");
                                tvTimeChats.text = pre.format(time).toString()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

            llUserChats.setOnClickListener {
                userClickListener.onUserClicked(user)
            }
        }

    }

}