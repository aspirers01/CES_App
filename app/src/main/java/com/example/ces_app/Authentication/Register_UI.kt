package com.example.ces_app.Authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ces_app.MainActivity
import com.example.ces_app.R
import com.example.ces_app.databinding.ActivityRegisterUiBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Register_UI : AppCompatActivity(), View.OnFocusChangeListener {
    private lateinit var binding: ActivityRegisterUiBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterUiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getting firebase authentication instance
        auth = FirebaseAuth.getInstance()

        val name = binding.fullNameEt
        val email = binding.emailEt
        val roll = binding.rollEt
        val password = binding.passwordEt
        val confirm = binding.confirmPasswordEt
        val actionbutton = binding.register

        name.onFocusChangeListener = this
        email.onFocusChangeListener = this
        roll.onFocusChangeListener = this
        password.onFocusChangeListener = this

        //enabling sign in button only when all fields are filled and validated
        val mTextWatcher = object : TextWatcher {
            override fun afterTextChanged(et: Editable?) {
                actionbutton.isEnabled = checkAllFields()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }
        name.addTextChangedListener(mTextWatcher)
        email.addTextChangedListener(mTextWatcher)
        roll.addTextChangedListener(mTextWatcher)
        password.addTextChangedListener(mTextWatcher)
        confirm.addTextChangedListener(mTextWatcher)




        val login = findViewById<TextView>(R.id.sign_in)
        login.setOnClickListener{
            val intent = Intent(this, Login_UI::class.java)
            startActivity(intent)
            finish()
        }

        //sign up button
        binding.register.setOnClickListener{
            val emails = binding.emailEt.text.toString()
            val passwords = binding.passwordEt.text.toString()
            if(checkAllFields() && passwordConfirmed()) {
                auth.createUserWithEmailAndPassword(emails, passwords).addOnCompleteListener{
                    if(it.isSuccessful) {
                        val success = "Account created successfully"
                        Toast.makeText(this, success, Toast.LENGTH_LONG).show()
                        actionbutton.isEnabled = false
                    }
                    else {
                        val addError = AlertDialog.Builder(this)
                            .setMessage("Account already registered")
                            .setPositiveButton("OK") { _, _ ->

                            }.create()
                        addError.show()
                        actionbutton.isEnabled = false
                        Log.e("error: ", it.exception.toString())
                    }
                }
            }
            else {
                actionbutton.isEnabled = false
                binding.confirmPasswordTil.error = "Password does not match"
                binding.confirmPasswordTil.isErrorEnabled = true
                val watcher = object : TextWatcher{
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int ){
                    }

                    override fun afterTextChanged(s: Editable?) {
                        binding.confirmPasswordTil.isErrorEnabled = false
                    }

                }
                confirm.addTextChangedListener(watcher)
            }
        }
    }



    private fun validateEmail() : Boolean {
        var actionButton = binding.register
        val email = binding.emailEt.text.toString()
        var errorMessage: String? = null

        if(email.isEmpty()) {
            errorMessage = null
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            actionButton.isEnabled = false
            errorMessage = "Invalid email"
        }

        if(errorMessage != null) {
            binding.emailTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }

        return errorMessage == null
    }

    private fun validateRoll() : Boolean {
        val roll = binding.rollEt.text.toString()
        var errorMessage: String? = null

        if(roll.isEmpty()) {
            errorMessage = null
        }

        else if(roll.length != 10) {
            errorMessage = "Invalid roll number"
        }

        if(errorMessage != null) {
            binding.rollTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }

        return errorMessage == null
    }

    private fun validatePassword() : Boolean {
        val pwd = binding.passwordEt.text.toString()
        var errorMessage: String? = null

        if(pwd.isEmpty()) {
            errorMessage = null
        }

        else if(pwd.length < 8) {
            errorMessage = "Password must be more than 8 characters"
        }

        if(errorMessage != null) {
            binding.passwordTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }

        return errorMessage == null
    }

    private fun passwordConfirmed() : Boolean{
        val pwd = binding.passwordEt.text.toString()
        val checkpwd = binding.confirmPasswordEt.text.toString()
        if (pwd != checkpwd) {
            return false
        }
        return true
    }


    private fun checkAllFields() : Boolean {
        val name = binding.fullNameEt.text.toString()
        val email = binding.emailEt.text.toString()
        val roll = binding.rollEt.text.toString()
        val password = binding.passwordEt.text.toString()
        val confirm = binding.confirmPasswordEt.text.toString()
        if (email.isEmpty() || name.isEmpty() || roll.isEmpty() || password.isEmpty() || confirm.isEmpty())
            return false
        else if(password.length < 8) return false
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false
        else if(roll.length != 10) return false
        return true
    }




    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        val actionButton = binding.register
        if (view != null){
            when(view.id) {
                R.id.rollEt -> {
                    if(hasFocus) {
                        if(binding.rollTil.isErrorEnabled) {
                            binding.rollTil.isErrorEnabled = false
                        }
                    }else {
                        if(!validateRoll()) {
                            actionButton.isEnabled = false
                        }
                        validateRoll()
                    }
                }
                R.id.emailEt -> {
                    if(hasFocus) {
                        if(binding.emailTil.isErrorEnabled) {
                            binding.emailTil.isErrorEnabled = false
                        }
                    }else {
                        if(!validateEmail()) {
                            actionButton.isEnabled = false
                        }
                        validateEmail()
                    }
                }
                R.id.passwordEt -> {
                    if(hasFocus) {
                        if(binding.passwordTil.isErrorEnabled) {
                            binding.passwordTil.isErrorEnabled = false
                        }
                    }else {
                        if(!validatePassword()) {
                            actionButton.isEnabled = false
                        }
                        validatePassword()
                    }
                }
            }
        }
    }



}