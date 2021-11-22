package com.capstone.attendance

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.attendance.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
    }
}