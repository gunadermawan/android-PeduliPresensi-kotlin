package com.capstone.attendance.ui.signup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.attendance.databinding.ActivitySignupBinding

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
    }
}