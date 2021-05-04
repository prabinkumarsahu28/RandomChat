package com.example.randomchat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.randomchat.R
import com.example.randomchat.models.Messages
import com.example.randomchat.viewholders.ReceiverChatViewHolder
import com.example.randomchat.viewholders.SenderChatViewHolder
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(private var messageList: List<Messages>, private var receiverId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var SENDER_VIEW = 1
    private var RECEIVER_VIEW = 2

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].uId == FirebaseAuth.getInstance().uid) {
            SENDER_VIEW
        } else {
            RECEIVER_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SENDER_VIEW) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.sender_sample_layout, parent, false)
            SenderChatViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.receiver_sample_layout, parent, false)
            ReceiverChatViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messages = messageList[position]
        if (holder.javaClass == SenderChatViewHolder::class.java) {
            (holder as SenderChatViewHolder).setData(messages, receiverId)
        } else {
            (holder as ReceiverChatViewHolder).setData(messages, receiverId)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}