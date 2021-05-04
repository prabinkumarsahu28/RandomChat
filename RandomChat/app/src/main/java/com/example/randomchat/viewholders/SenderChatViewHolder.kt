package com.example.randomchat.viewholders

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.randomchat.R
import com.example.randomchat.models.Messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.receiver_sample_layout.view.*
import kotlinx.android.synthetic.main.sender_sample_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class SenderChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun setData(messages: Messages, receiverId: String) {
        itemView.apply {
            if (messages.imgUrl != null) {
                imgSender.visibility = View.VISIBLE
                Glide.with(this)
                    .load(messages.imgUrl)
                    .placeholder(R.drawable.image_placeholder)
                    .centerCrop()
                    .into(imgSender)
            }

            tvSenderMessageChat.text = messages.message

            val time = Date(messages.timeStamp!!)
            val pre = SimpleDateFormat("KK:mm a");
            tvSenderMessageTimeChat.text = pre.format(time).toString()

            clSenderMessage.setOnLongClickListener {
                val builder = AlertDialog.Builder(context)

                builder.setMessage("Delete message?")

                builder.setPositiveButton("DELETE") { dialogInterface, which ->
                    val database = FirebaseDatabase.getInstance()
                    val branch = FirebaseAuth.getInstance().uid + receiverId
                    database.reference
                        .child("Chats")
                        .child(branch)
                        .child(messages.messageId!!)
                        .setValue(null)

                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                }

                builder.setNeutralButton("CANCEL") { dialogInterface, which ->

                }

                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()

                return@setOnLongClickListener false
            }
        }
    }
}