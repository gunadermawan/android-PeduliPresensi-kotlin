package com.capstone.attendance.ui.signup

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.capstone.attendance.R
import com.capstone.attendance.databinding.ActivitySignupBinding
import com.capstone.attendance.ui.login.LoginActivity
import com.capstone.attendance.ui.main.MainActivity
import com.capstone.attendance.utils.*
import com.google.firebase.auth.FirebaseAuth
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class SignupActivity : AppCompatActivity() {
    private lateinit var signupBinding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(signupBinding.root)
        supportActionBar?.hide()
        signupBinding.txtInputEmail.requestFocus()
        auth = FirebaseAuth.getInstance()
        signupBinding.btnRegister.setOnClickListener {
            if (FunctionLibrary.checkConnection(this)) {
                signupBinding.pbSignup.visibility = View.VISIBLE
                signupBinding.tvProcessLogin.visibility = View.VISIBLE
                val email = signupBinding.etEmail.text.toString().trim()
                val pass = signupBinding.etPassword.text.toString().trim()
                when {
                    email.isEmpty() -> {
                        signupBinding.pbSignup.visibility = View.GONE
                        signupBinding.tvProcessLogin.visibility = View.GONE
                        signupBinding.txtInputEmail.error = EMAIL_EMPTY
                        signupBinding.txtInputEmail.requestFocus()
                        return@setOnClickListener
                    }
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        signupBinding.pbSignup.visibility = View.GONE
                        signupBinding.tvProcessLogin.visibility = View.GONE
                        signupBinding.txtInputEmail.error = EMAIL_NOT_VALID
                        signupBinding.txtInputEmail.requestFocus()
                        return@setOnClickListener
                    }
                    pass.isEmpty() || pass.length < 8 -> {
                        signupBinding.pbSignup.visibility = View.GONE
                        signupBinding.tvProcessLogin.visibility = View.GONE
                        signupBinding.txtInputPassword.error = PASSWORD_LENGTH
                        signupBinding.txtInputPassword.requestFocus()
                        return@setOnClickListener
                    }
                    pass.length < 8 -> {
                        signupBinding.pbSignup.visibility = View.GONE
                        signupBinding.tvProcessLogin.visibility = View.GONE
                        signupBinding.txtInputPassword.error = PASSWORD_LENGTH
                        signupBinding.txtInputPassword.requestFocus()
                        return@setOnClickListener
                    }
                }
                registerUser(email, pass)
            } else {
                signupBinding.pbSignup.visibility = View.GONE
                signupBinding.tvProcessLogin.visibility = View.GONE
                FunctionLibrary.checkConnection(this)
                FunctionLibrary.toast(
                    this,
                    TOAST_ERROR,
                    PERMISSION_INTERNET,
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helveticabold)
                )
            }

        }
        signupBinding.btnLogin.setOnClickListener {
            Intent(this@SignupActivity, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    private fun registerUser(email: String, pass: String) {
        signupBinding.pbSignup.visibility = View.VISIBLE
        signupBinding.tvProcessLogin.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Intent(this@SignupActivity, MainActivity::class.java).also { intent ->
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
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
                    signupBinding.pbSignup.visibility = View.GONE
                    signupBinding.tvProcessLogin.visibility = View.GONE
                    FunctionLibrary.toast(
                        this,
                        TOAST_ERROR,
                        "${it.exception?.message}",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helveticabold)
                    )
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

}