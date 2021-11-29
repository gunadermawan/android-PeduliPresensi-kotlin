package com.capstone.attendance.ui.signup

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.capstone.attendance.MainActivity
import com.capstone.attendance.R
import com.capstone.attendance.databinding.ActivitySignupBinding
import com.capstone.attendance.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    private lateinit var signupBinding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(signupBinding.root)
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
        auth = FirebaseAuth.getInstance()
        signupBinding.btnRegister.setOnClickListener {
            val email = signupBinding.etEmail.text.toString().trim()
            val pass = signupBinding.etPassword.text.toString().trim()
            if (email.isEmpty()) {
                signupBinding.etEmail.error = "Email tidak boleh kosong!"
                signupBinding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                signupBinding.etEmail.error = "Email tidak valid!"
                signupBinding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if (pass.isEmpty() || pass.length < 8) {
                signupBinding.etPassword.error = "Password harus lebih dari 8 karakter"
                signupBinding.etPassword.requestFocus()
                return@setOnClickListener
            }
            registerUser(email, pass)

        }
        signupBinding.btnLogin.setOnClickListener {
            Intent(this@SignupActivity, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun registerUser(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Intent(this@SignupActivity, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                    val mNotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_finger)
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_finger))
                        .setContentTitle(resources.getString(R.string.register_succes))
                        .setContentText(resources.getString(R.string.register_succes_desc))
                        .setAutoCancel(true)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_HIGH
                        )
                        channel.description = CHANNEL_NAME
                        mBuilder.setChannelId(CHANNEL_ID)
                        mNotificationManager.createNotificationChannel(channel)
                    }
                    val notification = mBuilder.build()
                    mNotificationManager.notify(NOTIFICATION_ID, notification)
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            Intent(this@SignupActivity, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
    }
}