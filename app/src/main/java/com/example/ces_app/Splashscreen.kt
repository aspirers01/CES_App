package com.example.ces_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.ces_app.databinding.ActivitySplashscreenBinding

class Splashscreen : AppCompatActivity() {
    //creating a variable for view binding
    private lateinit var binding: ActivitySplashscreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding view to our activity
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //creating a delay for our intent
        Handler(Looper.getMainLooper()).postDelayed({
            //define a variable for intent passing
             val intent = Intent(this@Splashscreen, MainActivity::class.java)
            //passing intent
            startActivity(intent)
            //back button doesn't bring the previous activity back again
            finish()
        }, 3000)
    }
}