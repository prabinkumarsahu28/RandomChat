package com.example.randomchat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.randomchat.R
import com.example.randomchat.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        clickListeners()

    }

    private fun clickListeners() {
        btnSignUp.setOnClickListener {
            auth.createUserWithEmailAndPassword(
                etEmailSignUp.text.toString(),
                etPasswordSignUp.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = Users(
                        etNameSignUp.text.toString(),
                        etEmailSignUp.text.toString(),
                        etPasswordSignUp.text.toString()
                    )

                    val id = it.result?.user?.uid

                    database.reference.child("Users").child(id!!).setValue(user)

                    Toast.makeText(this, "Successfully SignedUp", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvAlreadyHaveSignUp.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}