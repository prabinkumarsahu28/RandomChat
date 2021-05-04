package com.example.randomchat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.randomchat.R
import com.example.randomchat.models.Users
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_phone_number_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class PhoneNumberLoginActivity : AppCompatActivity() {

    private var code: String? = null
    lateinit var verificationId: String
    lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number_login)
        supportActionBar?.hide()
        etOtp.isVisible = false
        btnProceed.isVisible = false

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        code = countryCode.fullNumber
        clickListeners()

    }

    private fun clickListeners() {
        btnSendOtp.setOnClickListener {

            if (etPhoneNumber.text.isEmpty() || etPhoneNumber.text.length < 10 || etUserName.text.isEmpty()) {
                etPhoneNumber.error = "Enter valid phone number"
                etPhoneNumber.requestFocus()
                etUserName.error = "Must fill"
            } else {
                etOtp.isVisible = true
                btnProceed.isVisible = true
                btnSendOtp.text = "Resend OTP"
                val phoneNumber = "+" + code + etPhoneNumber.text.toString()
                Log.d("msg", code.toString())
                Log.d("msg", phoneNumber)
                sendVerificationCode(phoneNumber)
            }
        }

        btnProceed.setOnClickListener {
            if (etOtp.text.isEmpty() || etOtp.text.length < 6) {
                etOtp.error = "Invalid code"
                etOtp.requestFocus()
                Toast.makeText(this, "Invalid code", Toast.LENGTH_SHORT).show()
            } else {
                verifyCode(etOtp.text.toString())
            }
        }

        btnSignInUsingEmail.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        btnSignUpUsingEmail.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun verifyCode(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {

                val user = Users(
                    etUserName.text.toString(),
                    etPhoneNumber.text.toString().toLong()
                )

                val id = it.result?.user?.uid

                database.reference.child("Users").child(id!!).setValue(user)
                Toast.makeText(this, "Successfully SignedUp", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            } else {
                Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,
            120,
            TimeUnit.SECONDS,
            this,
            callback)
    }

    private val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onCodeSent(veriId: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(veriId, p1)
            verificationId = veriId
        }

        override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            if (p0.smsCode != null) {
                verifyCode(p0.smsCode!!)
            }
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Toast.makeText(this@PhoneNumberLoginActivity, p0.message.toString(), Toast.LENGTH_SHORT)
                .show()
        }

    }
}
