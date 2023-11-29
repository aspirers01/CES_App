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
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getting current user
        val user = Firebase.auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl
            val uid = it.uid
        }
        var name = user?.displayName
        var domain: String? = null
        val email = user?.email

        //setting username as email address's name before @
        if (email != null) {
            val index = email.indexOf('@')
            domain = if (index == -1) null else email.substring(0, index)
        }

        if (name == null) {
            name = domain
        }
        val photoUrl = user?.photoUrl
        val userNameTil = binding.userName
        val userEmailTil = binding.userEmail
        val profilePicture = binding.profileImage
        userNameTil.text = name
        userEmailTil.text = email
        if (photoUrl == null) {
            val uri: Uri =
                Uri.parse("android.resource://" + packageName + "/" + "/drawable/ic_profile")
            Glide.with(this).load(uri).into(profilePicture)
        } else {
            Glide.with(this).load(photoUrl).into(profilePicture)
        }


        val exit = binding.exit

        val signout = binding.signOut

        signout.setOnClickListener {
            Firebase.auth.signOut()
            Toast.makeText(this, "Signed Out!", Toast.LENGTH_LONG).show()
            intent = Intent(this, Login_UI::class.java)
            startActivity(intent)
            finish()
        }

        exit.setOnClickListener {
            finish()
        }

    }
}