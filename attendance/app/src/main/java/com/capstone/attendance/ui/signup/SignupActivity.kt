package com.capstone.attendance.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.attendance.MainActivity
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
//        remove actionbar
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
//        firebase
        auth = FirebaseAuth.getInstance()
        signupBinding.btnSignUp.setOnClickListener {
//            fetch from edit_text
            val email = signupBinding.etEmail.text.toString().trim()
            val pass = signupBinding.etPassword.text.toString().trim()
//            validation
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
//            register this edit text to firebase
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
                } else {
                    Toast.makeText(this, "$it.exception?.message", Toast.LENGTH_SHORT).show()
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