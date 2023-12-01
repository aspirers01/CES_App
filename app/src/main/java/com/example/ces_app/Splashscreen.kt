package com.example.ces_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.ces_app.Authentication.Login_UI
import com.example.ces_app.databinding.ActivitySplashscreenBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
            //define a variable for getting current user from firebase
            val user = Firebase.auth.currentUser

            //if user found directly go to main activity
            if(user != null) {
                //passing intent
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                //back button doesn't bring the previous activity back again
                finish()
            }

            //if user not found go to login page
            else {
                val intent = Intent(this@Splashscreen, Login_UI::class.java)
                startActivity(intent)
                finish()
            }
            finish()
        }, 3000)
    }
}