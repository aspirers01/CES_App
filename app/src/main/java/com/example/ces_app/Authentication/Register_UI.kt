package com.example.ces_app.Authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ces_app.R
import com.example.ces_app.databinding.ActivityMainBinding
import com.example.ces_app.databinding.ActivityRegisterUiBinding

class Register_UI : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterUiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding= ActivityRegisterUiBinding.inflate(layoutInflater);
         setContentView(binding.root)
    }
}