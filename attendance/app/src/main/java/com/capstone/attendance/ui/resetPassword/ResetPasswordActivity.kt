package com.capstone.attendance.ui.resetPassword

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.capstone.attendance.R
import com.capstone.attendance.databinding.ActivityResetPasswordBinding
import com.capstone.attendance.ui.login.LoginActivity
import com.capstone.attendance.utils.*
import com.google.firebase.auth.FirebaseAuth
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.btnReset.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                binding.txtInputEmail.error = EMAIL_EMPTY
                binding.txtInputEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.txtInputEmail.error = EMAIL_NOT_VALID
                binding.txtInputEmail.requestFocus()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    FunctionLibrary.toast(
                        this,
                        TOAST_SUCCESS,
                        EMAIL_CONFIRM,
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helveticabold)
                    )
                    Intent(this@ResetPasswordActivity, LoginActivity::class.java).also {intent->
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    binding.txtInputEmail.error = EMAIL_NOT_REGISTERED
                    binding.txtInputEmail.requestFocus()
                }
            }
        }
    }
}