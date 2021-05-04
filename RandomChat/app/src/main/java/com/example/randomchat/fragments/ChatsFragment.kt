package com.example.randomchat.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.randomchat.R
import com.example.randomchat.activities.ChatActivity
import com.example.randomchat.adapters.StatusAdapter
import com.example.randomchat.adapters.UserAdapter
import com.example.randomchat.clicklisteners.UserClickListener
import com.example.randomchat.models.Status
import com.example.randomchat.models.UserStatus
import com.example.randomchat.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_chats.*
import java.util.*
import kotlin.collections.HashMap


class ChatsFragment : Fragment(), UserClickListener {

    private val userList = mutableListOf<Users>()
    private val statuses = mutableListOf<Status>()
    private val userStatusList = mutableListOf<UserStatus>()
    private lateinit var dialog: ProgressDialog
    lateinit var user: Users
    lateinit var database: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var userAdapter: UserAdapter
    lateinit var statusAdapter: StatusAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        dialog = ProgressDialog(activity)
        dialog.setMessage("Uploading image...")
        dialog.setCancelable(false)

        userAdapter = UserAdapter(userList, this)
        recyclerViewChats.layoutManager = LinearLayoutManager(activity)
        recyclerViewChats.adapter = userAdapter
        recyclerViewChats.showShimmerAdapter()

        statusAdapter = StatusAdapter(userStatusList, activity?.supportFragmentManager)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        statusRecyclerView.layoutManager = layoutManager
        statusRecyclerView.adapter = statusAdapter
        statusRecyclerView.showShimmerAdapter()

        clickListeners()
        getUserListFromDatabase()
        userObjectForStatus()
        getStatuses()

    }

    private fun getStatuses() {
        database.reference.child("Status").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                statusRecyclerView.hideShimmerAdapter()
                if (snapshot.exists()) {
                    userStatusList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val name = dataSnapshot.child("name").value.toString()
                        val dp = dataSnapshot.child("dp").value.toString()
                        val lastUpdated =
                            dataSnapshot.child("lastUpdated").value.toString().toLong()

                        val userStatus = UserStatus(name, dp, lastUpdated)

                        statuses.clear()
                        for (storySnapshot in dataSnapshot.child("stories").children) {
                            statuses.add(storySnapshot.getValue(Status::class.java)!!)
                        }

                        userStatus.statuses = statuses
                        userStatusList.add(userStatus)
                    }
                    statusAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getUserListFromDatabase() {
        database.reference.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    userList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val user = dataSnapshot.getValue(Users::class.java)
                        user?.userId = dataSnapshot.key

                        if (!user?.userId.equals(FirebaseAuth.getInstance().uid)) {
                            userList.add(user!!)
                        }
                    }
                    recyclerViewChats.hideShimmerAdapter()
                    userAdapter.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun userObjectForStatus() {
        database.reference.child("Users").child(auth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(Users::class.java)!!
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun clickListeners() {
        btnAddStatus.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 2)
        }
    }

    override fun onUserClicked(user: Users) {
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data?.data != null) {
            dialog.show()
            val storage = FirebaseStorage.getInstance()

            val reference =
                storage.reference.child("Status").child(Date().time.toString())

            reference.putFile(data.data!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reference.downloadUrl.addOnSuccessListener {
                        val userStatus = UserStatus(user.userName, user.dp, Date().time)

                        val hashMap = HashMap<String, Any>()
                        hashMap["name"] = userStatus.name!!
                        if (userStatus.dp != null) {
                            hashMap["dp"] = userStatus.dp!!
                        } else {
                            hashMap["dp"] = R.drawable.ic_user.toString()
                        }
                        hashMap["lastUpdated"] = userStatus.lastUpdated

                        val status = Status(it.toString(), userStatus.lastUpdated)

                        database.reference.child("Status")
                            .child(auth.uid!!)
                            .updateChildren(hashMap)

                        database.reference.child("Status")
                            .child(auth.uid!!)
                            .child("stories")
                            .push()
                            .setValue(status)

                        dialog.dismiss()
                    }
                }
            }
        }
    }
}