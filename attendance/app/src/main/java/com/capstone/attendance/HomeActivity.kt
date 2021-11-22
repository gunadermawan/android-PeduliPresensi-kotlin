package com.capstone.attendance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.attendance.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
    }
}