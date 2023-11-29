package com.example.ces_app

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.ces_app.Authentication.Login_UI
import com.example.ces_app.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    //variable for view binding
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //variables for different views
        val userNameTil = binding.userName
        val userEmailTil = binding.userEmail
        val profilePicture = binding.profileImage
        val exit = binding.exit
        val signout = binding.signOut

        //getting current user
        val user = Firebase.auth.currentUser

        //fetching current user's data
        var name = user?.displayName
        val email = user?.email
        val photoUrl = user?.photoUrl

        //for storing username if not signed in by google
        var domain: String? = null

        //setting username as email address before @
        if (email != null) {
            val index = email.indexOf('@')
            domain = if (index == -1) null else email.substring(0, index)
        }
        if (name == null) {
            name = domain
        }

        //filling username in name textView
        userNameTil.text = name
        //filling email of user in email textView
        userEmailTil.text = email

        //if no profile photo in case of email password sign in, set uri as uri of drawable ic_profile
        if (photoUrl == null) {
            val uri: Uri =
                Uri.parse("android.resource://" + packageName + "/" + "/drawable/ic_profile")

            //set that image in profile picture imageView
            Glide.with(this).load(uri).into(profilePicture)
        } else {

            //set image received from google account in imageView
            Glide.with(this).load(photoUrl).into(profilePicture)
        }



        //on clicking signOut button
        signout.setOnClickListener {
            //firebase sign out
            Firebase.auth.signOut()

            //display sign out toast
            Toast.makeText(this, "Signed Out!", Toast.LENGTH_LONG).show()

            //intent for returning to Login activity
            intent = Intent(this, Login_UI::class.java)
            startActivity(intent)

            //finish previous activity
            finish()
        }

        //on clicking exit button, exit app
        exit.setOnClickListener {
            finish()
        }

    }
}