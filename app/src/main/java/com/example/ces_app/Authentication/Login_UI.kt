package com.example.ces_app.Authentication

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ces_app.MainActivity
import com.example.ces_app.R
import com.example.ces_app.databinding.ActivityLoginUiBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.BuildConfig

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await


class Login_UI : AppCompatActivity(), View.OnFocusChangeListener {

    private lateinit var binding: ActivityLoginUiBinding
    private lateinit var auth: FirebaseAuth
    private var oneTapClient: SignInClient? = null
    private var signInRequest: BeginSignInRequest? = null
    private var showOneTapUI = true

    //google one tap sign in enabling
    private val oneTapResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                try {
                    val credential = oneTapClient?.getSignInCredentialFromIntent(result.data)
                    val idToken = credential?.googleIdToken
                    when {
                        idToken != null -> {
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            val msg = "idToken: $idToken"
                            Log.d("one tap", msg)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        successUI(user)


                                        Log.d(TAG, "signInWithCredential:success")
                                        finish()

                                    } else {
                                        successUI(null)
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                                    }
                                }
                            Log.d(TAG, "Got ID token.")
                        }

                        else -> {

                            Log.d("one tap", "No ID token!")
                            Snackbar.make(binding.root, "No ID token!", Snackbar.LENGTH_INDEFINITE).show()
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d("one tap", "One-tap dialog was closed.")
                            // Don't re-prompt the user.
//                            Snackbar.make(binding.root, "One-tap dialog was closed.", Snackbar.LENGTH_INDEFINITE).show()
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d("one tap", "One-tap encountered a network error.")
                            // Try again or just ignore.
                            Snackbar.make(binding.root, "One-tap encountered a network error.", Snackbar.LENGTH_INDEFINITE).show()
                        }
                        else -> {
                            Log.d("one tap", "Couldn't get credential from result." +
                                    " (${e.localizedMessage})")
                            Snackbar.make(binding.root, "Couldn't get credential from result.\" +\n" +
                                    " (${e.localizedMessage})", Snackbar.LENGTH_INDEFINITE).show()
                        }
                    }
                }
            }

    private fun successUI(user: FirebaseUser?) {
        if(user == null) {
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(this, "Signed in Successfully!", Toast.LENGTH_LONG).show()
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginUiBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()


        setContentView(binding.root)


        val registerText = binding.signUpText
        registerText.setOnClickListener {
            val intent = Intent(this, Register_UI::class.java)
            startActivity(intent)
            finish()
        }


        val forgotPassword = binding.forgotPassword
        forgotPassword.setOnClickListener {
            val dialogLayout = LayoutInflater.from(this).inflate(R.layout.email_text, null)
            val addEmail = AlertDialog.Builder(this)
            addEmail.setView(dialogLayout)
            val dialog = addEmail.create()

            val submitEmail = dialogLayout.findViewById<Button>(R.id.emailSubmit)
            val cancelEmail = dialogLayout.findViewById<Button>(R.id.emailNotSubmit)
            val email = dialogLayout.findViewById<EditText>(R.id.emailForgot)
            val emailBox =
                dialogLayout.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.emailForgotParent)


            val mWatcher = object : TextWatcher {
                override fun afterTextChanged(et: Editable?) {
                    submitEmail.isEnabled = email.text.toString().isNotEmpty()
                    emailBox.isErrorEnabled = false

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            }
            email.addTextChangedListener(mWatcher)
            submitEmail.setOnClickListener {
                val emailText = email.text.toString()
                if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    emailBox.isErrorEnabled = true
                    emailBox.error = "Invalid email format"
                    submitEmail.isEnabled = false
                } else {
                    dialog.dismiss()
                    auth.sendPasswordResetEmail(emailText).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val emailSent = AlertDialog.Builder(this)
                                .setTitle("Email sent")
                                .setMessage("We have sent an email to $emailText. Click on the link in the mail to reset your password. ")
                                .setPositiveButton("OK") { _, _ ->

                                }.create()
                            emailSent.show()

                        } else {
                            Toast.makeText(this, "Some error occurred", Toast.LENGTH_LONG).show()
                            Log.e("error: ", it.exception.toString())
                        }
                    }
                }


            }
            cancelEmail.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()


        }

        val emailtext = binding.emailEt
        val password = binding.passwordEt
        val actionbutton = binding.signIn

        emailtext.onFocusChangeListener = this

        val mTextWatcher = object : TextWatcher {
            override fun afterTextChanged(et: Editable?) {
                actionbutton.isEnabled = checkFeatures()

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }
        emailtext.addTextChangedListener(mTextWatcher)
        password.addTextChangedListener(mTextWatcher)


        //sign in button

        binding.signIn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val passwords = binding.passwordEt.text.toString()
            if (checkFeatures()) {
                auth.signInWithEmailAndPassword(email, passwords).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Signed in successfully", Toast.LENGTH_LONG).show()
                        intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val addError = AlertDialog.Builder(this)
                            .setTitle("Sign in failed")
                            .setMessage("Incorrect email or password")
                            .setPositiveButton("OK") { _, _ ->

                            }.create()
                        addError.show()
                        actionbutton.isEnabled = false


                        Log.e("error: ", it.exception.toString())
                    }
                }
            }

        }
        //google sign in
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.default_web_client_id))
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()
        binding.signInGoogle.setOnClickListener {
            displaySignIn()
        }

    }


    private fun displaySignIn(){
        oneTapClient?.beginSignIn(signInRequest!!)
            ?.addOnSuccessListener(this) { result ->
                try {
                    val ib = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    oneTapResult.launch(ib)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("btn click", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            ?.addOnFailureListener(this) { e ->
                Toast.makeText(this, "No Google Accounts found", Toast.LENGTH_LONG).show()
                Log.d("btn click failed", e.localizedMessage!!)
            }


    }




    private fun validateEmail() : Boolean {
        val email = binding.emailEt.text.toString()
        var errorMessage: String? = null

        if(email.isEmpty()) {
            errorMessage = null
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Invalid email format"
        }

        if(errorMessage != null) {
            binding.emailTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }

        return errorMessage == null
    }





    private fun checkFeatures() : Boolean {
        val email = binding.emailEt.text.toString()
        val pass = binding.passwordEt.text.toString()
        if(email.isEmpty()) return false
        else if (pass.isEmpty()) return false
        else if (pass.length < 8) return false
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false
        return true
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null){
            when(view.id) {

                R.id.emailEt -> {
                    if(hasFocus) {
                        if(binding.emailTil.isErrorEnabled) {
                            binding.emailTil.isErrorEnabled = false
                        }
                    }else {
                        val actionButton = binding.signIn
                        if(!validateEmail()) {
                            actionButton.isEnabled = false
                        }
                        validateEmail()
                    }
                }
            }
        }
    }
}



