package com.example.randomchat.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.randomchat.R
import com.example.randomchat.adapters.ChatAdapter
import com.example.randomchat.models.Messages
import com.example.randomchat.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_chat.*
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL
import java.util.*


class ChatActivity : AppCompatActivity() {

    private val messagesList = mutableListOf<Messages>()
    private var sender: String? = null
    private var receiver: String? = null
    private var senderBranch: String? = null
    private var receiverBranch: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase
    lateinit var chatAdapter: ChatAdapter
    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()
        dialog = ProgressDialog(this)
        dialog.setMessage("Sending...")
        dialog.setCancelable(false)

        if (intent != null && intent.extras != null) {
            val user: Users = intent.getSerializableExtra("user") as Users

            sender = auth.uid
            receiver = user.userId
            senderBranch = sender + receiver
            receiverBranch = receiver + sender
            tvUserNameChat.text = user.userName
            Glide.with(this).load(user.dp).placeholder(R.drawable.ic_user).into(ivProfilePicChat)
        }

        chatAdapter = ChatAdapter(messagesList, receiver!!)
        recyclerViewUserChat.layoutManager = LinearLayoutManager(this)

        Handler().postDelayed({
            recyclerViewUserChat.scrollToPosition(chatAdapter.itemCount - 1)
        }, 500)

        recyclerViewUserChat.adapter = chatAdapter

        clickListeners()
        videoConference()
        userActiveStatus()
        userTypingStatus()
        userChattingMessages()
    }

    private fun userTypingStatus() {
        val handler = Handler()
        etTypeMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                database.reference.child("activity").child(auth.uid!!).setValue("typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(runnable, 1000)
            }

            val runnable = Runnable {
                database.reference.child("activity").child(auth.uid!!).setValue("Online")
            }

        })

    }

    private fun userChattingMessages() {
        database.reference.child("Chats").child(senderBranch!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messagesList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val model = dataSnapshot.getValue(Messages::class.java)
                        model?.messageId = dataSnapshot.key

                        messagesList.add(model!!)
                    }
                    chatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatActivity, "Cancelled", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun userActiveStatus() {
        database.reference.child("activity").child(receiver!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.value.toString() == "") {
                            tvActiveStatus.visibility = View.GONE
                        } else {
                            tvActiveStatus.text = snapshot.value.toString()
                            tvActiveStatus.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun videoConference() {
        try {
            val serverUrl = URL("https://meet.jit.si")

            val defaultOption = JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverUrl)
                .setWelcomePageEnabled(false)
                .build()

            JitsiMeet.setDefaultConferenceOptions(defaultOption)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        database.reference.child("activity").child(auth.uid!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        database.reference.child("activity").child(auth.uid!!).setValue("")
    }

    private fun clickListeners() {

        ivVideoCallChat.setOnClickListener {
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(auth.uid)
                .setWelcomePageEnabled(false)
                .build()

            JitsiMeetActivity.launch(this, options)
        }

        ivBackArrowChat.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        ivAttachment.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 3)
        }

        ivBtnSend.setOnClickListener {
            if (etTypeMessage.text.isNotEmpty()) {
                val message = etTypeMessage.text.toString()
                val messageModel = Messages(message, sender)
                messageModel.timeStamp = Date().time
                etTypeMessage.setText("")

                database.reference.child("Chats").child(senderBranch!!).push()
                    .setValue(messageModel)
                    .addOnSuccessListener {
                        database.reference.child("Chats").child(receiverBranch!!).push()
                            .setValue(messageModel).addOnSuccessListener {

                            }
                    }

                Handler().postDelayed({
                    recyclerViewUserChat.scrollToPosition(chatAdapter.itemCount - 1)
                }, 500)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data?.data != null) {
            val imgLoc = data.data
            val calendar = Calendar.getInstance()

            val reference: StorageReference =
                storage.reference.child("Chats").child(calendar.timeInMillis.toString())
            dialog.show()

            reference.putFile(imgLoc!!).addOnSuccessListener {
                dialog.dismiss()
                reference.downloadUrl.addOnSuccessListener {
                    var message = ""
                    if (etTypeMessage.text.isNotEmpty()) {
                        message = etTypeMessage.text.toString()
                    }
                    val messageModel = Messages(message, sender, it.toString())
                    messageModel.timeStamp = Date().time
                    etTypeMessage.setText("")

                    database.reference.child("Chats").child(senderBranch!!).push()
                        .setValue(messageModel)
                        .addOnSuccessListener {
                            database.reference.child("Chats").child(receiverBranch!!).push()
                                .setValue(messageModel).addOnSuccessListener {

                                }
                        }

                    Handler().postDelayed({
                        recyclerViewUserChat.scrollToPosition(chatAdapter.itemCount - 1)
                    }, 500)
                }
            }
        }
    }
}