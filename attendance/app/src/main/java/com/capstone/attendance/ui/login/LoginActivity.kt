package com.capstone.attendance.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.attendance.databinding.ActivityLoginBinding
import com.capstone.attendance.ui.signup.SignupActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
//        remove actionbar
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
        loginBinding.btnSignUp.setOnClickListener {
            Intent(this@LoginActivity, SignupActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}