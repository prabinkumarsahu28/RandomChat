package com.example.randomchat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.randomchat.R
import com.example.randomchat.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*
import kotlin.collections.HashMap

class SettingsActivity : AppCompatActivity() {

    lateinit var storage: FirebaseStorage
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.hide()
        cvEditProfile.isVisible = false

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        userDetails()
        clickListeners()

    }

    private fun userDetails() {
        database.reference.child("Users").child(auth.uid!!).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)

                Glide.with(this@SettingsActivity)
                    .load(user?.dp)
                    .placeholder(R.drawable.ic_user)
                    .into(profilePicProfile)

                tvNameProfile.text = user?.userName
                if (user?.about.isNullOrEmpty()) {
                    tvAboutProfile.text = ""
                } else {
                    tvAboutProfile.text = user?.about
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onResume() {
        super.onResume()
        database.reference.child("activity").child(auth.uid!!).setValue("Online")
    }

    private fun clickListeners() {

        btnLogout.setOnClickListener {
            database.reference.child("activity").child(auth.uid!!).setValue("")
            auth.signOut()
            val intent = Intent(this, PhoneNumberLoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        tvNameProfile.setOnClickListener {
            tvHeadingProfile.text = "Enter your name"
            etEditNameProfile.setText(tvNameProfile.text.toString())
            etEditAboutProfile.setText(tvAboutProfile.text.toString())
            cvEditProfile.isVisible = true
        }

        tvAboutProfile.setOnClickListener {
            tvHeadingProfile.text = "Enter your about"
            etEditNameProfile.setText(tvNameProfile.text.toString())
            etEditAboutProfile.setText(tvAboutProfile.text.toString())
            cvEditProfile.isVisible = true
        }

        ivBackArrowProfile.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        ivChoosePicProfile.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        tvSaveProfile.setOnClickListener {
            if (etEditNameProfile.text.isNotEmpty()) {
                val hashMap = HashMap<String, Any>()

                hashMap["userName"] = etEditNameProfile.text.toString()
                hashMap["about"] = etEditAboutProfile.text.toString()

                database.reference.child("Users").child(auth.uid!!).updateChildren(hashMap)
                tvNameProfile.text = etEditNameProfile.text.toString()
                tvAboutProfile.text = etEditAboutProfile.text.toString()

                cvEditProfile.isVisible = false

                Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show()
            } else {
                etEditNameProfile.error = "Can not be empty"
            }
        }

        tvCancelProfile.setOnClickListener {
            cvEditProfile.isVisible = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data?.data != null) {
            val imgLoc = data.data
            profilePicProfile.setImageURI(imgLoc)

            val reference: StorageReference = storage.reference.child("dp").child(auth.uid!!)

            reference.putFile(imgLoc!!).addOnSuccessListener {
                reference.downloadUrl.addOnSuccessListener {
                    database.reference.child("Users").child(auth.uid!!).child("dp")
                        .setValue(it.toString())
                }
            }
        }
    }

}