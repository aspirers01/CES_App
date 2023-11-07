package com.example.ces_app.Authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ces_app.R
import com.example.ces_app.databinding.ActivityLoginUiBinding
import com.example.ces_app.databinding.ActivityRegisterUiBinding

class Login_UI : AppCompatActivity() {

    private lateinit var binding: ActivityLoginUiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginUiBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}