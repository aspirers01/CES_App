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

    //variable for view binding
    private lateinit var binding: ActivityRegisterUiBinding

    //variable for firebase authentication
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterUiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getting firebase authentication instance
        auth = FirebaseAuth.getInstance()

        //variables for all views
        val name = binding.fullNameEt
        val email = binding.emailEt
        val roll = binding.rollEt
        val password = binding.passwordEt
        val confirm = binding.confirmPasswordEt

        //Sign Up button view
        val actionbutton = binding.register


        //focus change listeners for all input fields
        name.onFocusChangeListener = this
        email.onFocusChangeListener = this
        roll.onFocusChangeListener = this
        password.onFocusChangeListener = this

        //enabling sign in button only when all fields are filled and no error in validation
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



        //on clicking Back to SignIn text
        val login = binding.signIn
        login.setOnClickListener{

            //go back to login activity
            val intent = Intent(this, Login_UI::class.java)
            startActivity(intent)

            //don't come back again to this activity
            finish()
        }

        //on clicking sign up button
        binding.register.setOnClickListener{

            //getting email and password texts
            val emails = binding.emailEt.text.toString()
            val passwords = binding.passwordEt.text.toString()

            //if all fields are validated and confirm password matches password
            if(checkAllFields() && passwordConfirmed()) {

                //sign up with given email and password
                auth.createUserWithEmailAndPassword(emails, passwords).addOnCompleteListener{

                    //account created successfully
                    if(it.isSuccessful) {
                        val success = "Account created successfully"

                        //switch to signIn activity
                        intent = Intent(this, Login_UI::class.java)
                        startActivity(intent)

                        //display toast
                        Toast.makeText(this, success, Toast.LENGTH_LONG).show()

                        //don't come back to this activity
                        finish()
                    }
                    else {

                        //if account is already registered, show alert dialog
                        val addError = AlertDialog.Builder(this)
                            .setMessage("Account already registered")
                            .setPositiveButton("OK") { _, _ ->

                            }.create()
                        addError.show()

                        //disable signUp button until text changed
                        actionbutton.isEnabled = false
                        Log.e("error: ", it.exception.toString())
                    }
                }
            }

            //if confirm password does not match the entered password
            else {
                //disable signUp button
                actionbutton.isEnabled = false

                //show error under confirm password field
                binding.confirmPasswordTil.error = "Password does not match"
                binding.confirmPasswordTil.isErrorEnabled = true

                //enable button only when confirm password text is changed
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


    //validating email
    private fun validateEmail() : Boolean {
        val email = binding.emailEt.text.toString()
        var errorMessage: String? = null

        if(email.isEmpty()) {
            errorMessage = null
        }

        //if email entered doesn't match the email format
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            actionButton.isEnabled = false
            errorMessage = "Invalid email"
        }

        //if error message is not null, enable error and errorMessage is displayed as error
        if(errorMessage != null) {
            binding.emailTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }

        return errorMessage == null
    }

    //validating roll number
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

    //validate password
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

    //validating if confirm password and password is matching
    private fun passwordConfirmed() : Boolean{
        val pwd = binding.passwordEt.text.toString()
        val checkpwd = binding.confirmPasswordEt.text.toString()
        if (pwd != checkpwd) {
            return false
        }
        return true
    }

    //checkAllFields for enabling or disabling signUp button and whether to continue for registering
    private fun checkAllFields() : Boolean {1234

        //getting all texts entered
        val name = binding.fullNameEt.text.toString()
        val email = binding.emailEt.text.toString()
        val roll = binding.rollEt.text.toString()
        val password = binding.passwordEt.text.toString()
        val confirm = binding.confirmPasswordEt.text.toString()

        //validate every field through given conditions

        //if any field is empty
        if (email.isEmpty() || name.isEmpty() || roll.isEmpty() || password.isEmpty() || confirm.isEmpty())
            return false


        else if(password.length < 8) return false
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false
        else if(roll.length != 10) return false

        return true
    }



    //on changing focus from one field to another

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        val actionButton = binding.register
        if (view != null){
            when(view.id) {

                //in rollNumber field
                R.id.rollEt -> {

                    //if focus is on the field in which text is being entered or changed, disable error
                    if(hasFocus) {
                        if(binding.rollTil.isErrorEnabled) {
                            binding.rollTil.isErrorEnabled = false
                        }
                    }else {
                        if(!validateRoll()) {
                            //disable signUp button if error
                            actionButton.isEnabled = false
                        }

                        //validate and display if any errors
                        validateRoll()
                    }
                }

                //in email field
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

                //in password field
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