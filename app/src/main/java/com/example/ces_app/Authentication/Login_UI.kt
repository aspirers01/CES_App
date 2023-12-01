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

    //variable for firebase authentication
    private lateinit var auth: FirebaseAuth

    //variables for sign in client and begin sign in client for google authentication
    private var oneTapClient: SignInClient? = null
    private var signInRequest: BeginSignInRequest? = null



    //Receiving Google one tap SignIn Intent
    //Activity contract for receiving intent sent from google
    private val oneTapResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                try {
                    //parameter result is the intent received
                    //getting user credential from intent
                    val credential = oneTapClient?.getSignInCredentialFromIntent(result.data)

                    //getting token of user
                    val idToken = credential?.googleIdToken

                    when {
                        //if token is not null, successfully got user
                        idToken != null -> {

                            //firebaseCredential takes credential from token
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

                            //for tracking in logcat
                            val msg = "idToken: $idToken"
                            Log.d("one tap", msg)

                            //signing in with received credentials through firebase
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->

                                    //if sign in successful
                                    if (task.isSuccessful) {

                                        //getting google signed in user
                                        val user = auth.currentUser

                                        //calling successUI function with user parameter
                                        successUI(user)

                                        //tracking successful signIn in logcat
                                        Log.d(TAG, "signInWithCredential:success")

                                    } else {

                                        //if user sign in failed send null
                                        successUI(null)

                                        //tracking if sign in failed
                                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                                    }
                                }
                            Log.d(TAG, "Got ID token.")
                        }

                        //if no token received
                        else -> {

                            //track in log cat
                            Log.d("one tap", "No ID token!")

                            //display a Snackbar
                            Snackbar.make(binding.root, "No ID token!", Snackbar.LENGTH_INDEFINITE).show()
                        }
                    }
                } catch (e: ApiException) {

                    //tracking all sorts of errors
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d("one tap", "One-tap dialog was closed.")
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

    //displaying toasts through successUI function for sign in failed or successful
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

        //registerText binds sign up textView
        val registerText = binding.signUpText

        //on clicking sign up text, go to SignUp or Registration activity
        registerText.setOnClickListener {
            val intent = Intent(this, Register_UI::class.java)
            startActivity(intent)
            //finish this activity so that user doesn't return to login activity
            finish()
        }

        //Forgot Password Code

        //binding forgot password to forgotPassword
        val forgotPassword = binding.forgotPassword

        //on clicking forgot password text
        forgotPassword.setOnClickListener {

            //setting up custom dialog box, getting layout for dialog box from email_text.xml
            val dialogLayout = LayoutInflater.from(this).inflate(R.layout.email_text, null)

            //setting up dialog builder in forgotPasswordDialog
            val forgotPasswordDialog = AlertDialog.Builder(this)

            //setting view of dialog box as in email_text.xml
            forgotPasswordDialog.setView(dialogLayout)
            val dialog = forgotPasswordDialog.create()

            //variables for different views of email_text.xml
            val submitDialog = dialogLayout.findViewById<Button>(R.id.emailSubmit)
            val cancelDialog = dialogLayout.findViewById<Button>(R.id.emailNotSubmit)
            val email = dialogLayout.findViewById<EditText>(R.id.emailForgot)
            val emailBox = dialogLayout.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.emailForgotParent)

            //enabling / disabling sign in button if emailText is empty or invalid
            val mWatcher = object : TextWatcher {
                override fun afterTextChanged(et: Editable?) {
                    submitDialog.isEnabled = email.text.toString().isNotEmpty()

                    //disabling error when inside emailBox on changing text
                    emailBox.isErrorEnabled = false

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            }
            email.addTextChangedListener(mWatcher)

            //on clicking Continue button
            submitDialog.setOnClickListener {

                //getting the email in emailText
                val emailText = email.text.toString()

                //validating email
                if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    emailBox.isErrorEnabled = true
                    emailBox.error = "Invalid email format"
                    submitDialog.isEnabled = false
                } else {

                    //cancel dialog
                    dialog.dismiss()

                    //send a password reset email to the registered email
                    auth.sendPasswordResetEmail(emailText).addOnCompleteListener {
                        if (it.isSuccessful) {

                            //creating an email sent alert dialog
                            val emailSent = AlertDialog.Builder(this)
                                .setTitle("Email sent")
                                .setMessage("We have sent an email to $emailText. Click on the link in the mail to reset your password. ")
                                .setPositiveButton("OK") { _, _ ->

                                }.create()
                            emailSent.show()

                        } else {

                            //there will never be an error, but just in case something unexpected happens
                            Toast.makeText(this, "Some error occurred", Toast.LENGTH_LONG).show()
                            Log.e("error: ", it.exception.toString())
                        }
                    }
                }


            }

            //on clicking Cancel button
            cancelDialog.setOnClickListener {
                dialog.dismiss()
            }

            //show alert dialog
            dialog.show()


        }

        //variables for each view
        val emailtext = binding.emailEt
        val password = binding.passwordEt

        //view of signIn button in actionbutton
        val actionbutton = binding.signIn

        //a focus change listener in email input field
        emailtext.onFocusChangeListener = this


        //enabling/disabling signIn button if checkFeatures is true(all fields not empty and no error in any field)
        val mTextWatcher = object : TextWatcher {
            override fun afterTextChanged(et: Editable?) {
                //also disable signIn button if password is less than 8 characters
                actionbutton.isEnabled = checkFeatures()

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }
        emailtext.addTextChangedListener(mTextWatcher)
        password.addTextChangedListener(mTextWatcher)

        //Email Password Sign in

        //on clicking sign in button
        actionbutton.setOnClickListener {

            //getting email and password texts
            val email = binding.emailEt.text.toString()
            val passwords = binding.passwordEt.text.toString()

            //if all fields are not empty and no error
            if (checkFeatures()) {

                //sign in with the provided credentials
                auth.signInWithEmailAndPassword(email, passwords).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Signed in successfully", Toast.LENGTH_LONG).show()
                        intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {

                        //if password or email doesn't match, display alert dialog
                        val addError = AlertDialog.Builder(this)
                            .setTitle("Sign in failed")
                            .setMessage("Incorrect email or password")
                            .setPositiveButton("OK") { _, _ ->

                            }.create()
                        addError.show()

                        //disable sign In button temporarily
                        actionbutton.isEnabled = false


                        Log.e("error: ", it.exception.toString())
                    }
                }
            }

        }

        //Google One-Tap Sign in

        oneTapClient = Identity.getSignInClient(this)

        //setting up dialog for one-tap sign in
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()

        //when SignIn with Google button is clicked
        binding.signInGoogle.setOnClickListener {
            displaySignIn()
        }

    }


    private fun displaySignIn(){
        oneTapClient?.beginSignIn(signInRequest!!)

            //if successfully opened one-tap signIn dialog
            ?.addOnSuccessListener(this) { result ->
                try {
                    val ib = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    oneTapResult.launch(ib)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("btn click", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
                //if couldn't open one-tap signIn dialog
            ?.addOnFailureListener(this) { e ->

                //display a toast for no google accounts found, if none added in device
                Toast.makeText(this, "No Google Accounts found", Toast.LENGTH_LONG).show()
                Log.d("btn click failed", e.localizedMessage!!)
            }


    }



    //validating email text
    private fun validateEmail() : Boolean {
        //get email
        val email = binding.emailEt.text.toString()
        var errorMessage: String? = null

        if(email.isEmpty()) {
            //do not display any error message if left null
            errorMessage = null
        }

        //if email not in email address format
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Invalid email format"
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




    //validating through checkFeatures function for enabling or disabling signIn button and continue to signIn
    private fun checkFeatures() : Boolean {

        //get all input texts
        val email = binding.emailEt.text.toString()
        val pass = binding.passwordEt.text.toString()

        //validate every field through given conditions
        if(email.isEmpty()) return false
        else if (pass.isEmpty()) return false
        else if (pass.length < 8) return false
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false
        return true
    }



    //when focus is changed from one field to another
    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null){
            when(view.id) {

                R.id.emailEt -> {
                    if(hasFocus) {
                        //dont display error if already on that field
                        if(binding.emailTil.isErrorEnabled) {
                            binding.emailTil.isErrorEnabled = false
                        }
                    }else {
                        val actionButton = binding.signIn
                        if(!validateEmail()) {
                            //disable action button if email not validated
                            actionButton.isEnabled = false
                        }

                        //displaying or not displaying error when focus changed, according to validation
                        validateEmail()
                    }
                }
            }
        }
    }
}



