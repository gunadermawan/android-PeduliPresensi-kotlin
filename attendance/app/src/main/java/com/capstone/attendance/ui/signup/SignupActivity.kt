package com.capstone.attendance.ui.signup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.attendance.databinding.ActivitySignupBinding
import com.capstone.attendance.ui.login.LoginActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var signupBinding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(signupBinding.root)
//        remove actionbar
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
        signupBinding.btnLogin.setOnClickListener {
            Intent(this@SignupActivity, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}