package com.example.ces_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.ces_app.databinding.ActivitySplashscreenBinding

class Splashscreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashscreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({
             val intent = Intent(this@Splashscreen, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}