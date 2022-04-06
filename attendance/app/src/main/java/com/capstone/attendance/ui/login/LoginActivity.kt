package com.capstone.attendance.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.capstone.attendance.R
import com.capstone.attendance.databinding.ActivityLoginBinding
import com.capstone.attendance.ui.main.MainActivity
import com.capstone.attendance.ui.resetPassword.ResetPasswordActivity
import com.capstone.attendance.ui.signup.SignupActivity
import com.capstone.attendance.utils.*
import com.google.firebase.auth.FirebaseAuth
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
        loginBinding.txtInputEmail.requestFocus()
        auth = FirebaseAuth.getInstance()
        loginBinding.btnLogin.setOnClickListener {
            if (FunctionLibrary.checkConnection(this)) {
                loginBinding.pbLogin.visibility = View.VISIBLE
                loginBinding.tvProcessLogin.visibility = View.VISIBLE
                val email = loginBinding.etEmail.text.toString().trim()
                val pass = loginBinding.etPassword.text.toString().trim()
                when {
                    email.isEmpty() -> {
                        loginBinding.pbLogin.visibility = View.GONE
                        loginBinding.tvProcessLogin.visibility = View.GONE
                        loginBinding.txtInputEmail.error = EMAIL_EMPTY
                        loginBinding.txtInputEmail.requestFocus()
                        return@setOnClickListener
                    }
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        loginBinding.pbLogin.visibility = View.GONE
                        loginBinding.tvProcessLogin.visibility = View.GONE
                        loginBinding.txtInputEmail.error = EMAIL_NOT_VALID
                        loginBinding.txtInputEmail.requestFocus()
                        return@setOnClickListener
                    }
                    pass.isEmpty() -> {
                        loginBinding.pbLogin.visibility = View.GONE
                        loginBinding.tvProcessLogin.visibility = View.GONE
                        loginBinding.txtInputPassword.error = PASSWORD_EMPTY
                        loginBinding.txtInputPassword.requestFocus()
                        return@setOnClickListener
                    }
                    pass.length < 8 -> {
                        loginBinding.pbLogin.visibility = View.GONE
                        loginBinding.tvProcessLogin.visibility = View.GONE
                        loginBinding.txtInputPassword.error = PASSWORD_LENGTH
                        loginBinding.txtInputPassword.requestFocus()
                        return@setOnClickListener
                    }
                }
                loginUser(email, pass)
            } else {
                loginBinding.pbLogin.visibility = View.GONE
                loginBinding.tvProcessLogin.visibility = View.GONE
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
        loginBinding.btnRegister.setOnClickListener {
            Intent(this@LoginActivity, SignupActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
        loginBinding.tvForgotPass.setOnClickListener {
            Intent(this@LoginActivity, ResetPasswordActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun loginUser(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { login ->
                if (login.isSuccessful) {
                    Intent(this@LoginActivity, MainActivity::class.java).also { intent ->
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }
            .addOnFailureListener {
                loginBinding.pbLogin.visibility = View.GONE
                loginBinding.tvProcessLogin.visibility = View.GONE
                FunctionLibrary.toast(
                    this,
                    TOAST_ERROR,
                    "${it.message}",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helveticabold)
                )
                loginBinding.txtInputPassword.requestFocus()
            }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            Intent(this@LoginActivity, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }
}