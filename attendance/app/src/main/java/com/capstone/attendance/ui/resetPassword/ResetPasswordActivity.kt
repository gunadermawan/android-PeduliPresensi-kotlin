package com.capstone.attendance.ui.resetPassword

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.attendance.databinding.ActivityResetPasswordBinding
import com.capstone.attendance.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

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
                binding.txtInputEmail.error = "Email tidak boleh kosong!"
                binding.txtInputEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.txtInputEmail.error = "Email tidak valid!"
                binding.txtInputEmail.requestFocus()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Silahkan cek email Anda untuk reset password",
                        Toast.LENGTH_SHORT
                    ).show()
                    Intent(this@ResetPasswordActivity, LoginActivity::class.java).also {intent->
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    binding.txtInputEmail.error = "Email Anda tidak terdaftar!"
                    binding.txtInputEmail.requestFocus()
                }
            }
        }
    }
}