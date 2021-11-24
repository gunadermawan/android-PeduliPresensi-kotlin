package com.capstone.attendance.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.attendance.MainActivity
import com.capstone.attendance.databinding.ActivityLoginBinding
import com.capstone.attendance.ui.signup.SignupActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
//        remove actionbar
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
//        login with firebase
        auth = FirebaseAuth.getInstance()
        loginBinding.btnLogin.setOnClickListener {
//            validations
            //            fetch from edit_text
            val email = loginBinding.etEmail.text.toString().trim()
            val pass = loginBinding.etPassword.text.toString().trim()
//            validation
            if (email.isEmpty()) {
                loginBinding.etEmail.error = "Email tidak boleh kosong!"
                loginBinding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                loginBinding.etEmail.error = "Email tidak valid!"
                loginBinding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if (pass.isEmpty() || pass.length < 8) {
                loginBinding.etPassword.error = "Password harus lebih dari 8 karakter"
                loginBinding.etPassword.requestFocus()
                return@setOnClickListener
            }
//            login this edit text to firebase
            loginUser(email, pass)
        }
        loginBinding.btnSignUp.setOnClickListener {
            Intent(this@LoginActivity, SignupActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun loginUser(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Intent(this@LoginActivity, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {
                    Toast.makeText(this, "Email/Password salah!", Toast.LENGTH_SHORT).show()
                }
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