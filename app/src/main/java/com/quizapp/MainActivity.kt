package com.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCanceledListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.auth.api.signin.GoogleSignIn as GoogleSignIn


private val Any.signInIntent: Unit
    get() = Unit

class MainActivity : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN = 123
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    var callbackManager: CallbackManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_main)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()

        callbackManager = CallbackManager.Factory.create()

        login_button.setOnClickListener {
            logIn()
        }

        sign_in.setOnClickListener{
            signIn()
        }


        // To hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN


    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun logIn(){
        login_button.registerCallback(callbackManager, object: FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                handleFaceboockAccessToken(result!!.accessToken)
            }

            override fun onCancel() {
                
            }

            override fun onError(error: FacebookException?) {
                
            }

        })
    }

    private fun handleFaceboockAccessToken(accessToken: AccessToken?) {
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
        mAuth.signInWithCredential(credential)
            .addOnSuccessListener {result ->
                val intent = Intent(this, QuizQuestionsActivity::class.java)
                startActivity(intent)
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if(task.isSuccessful){
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("MainActivity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("MainActivity", "Google sign in failed", e)
                }
            }else {
                Log.w("MainActivity", exception.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                val any = if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("MainActivity", "signInWithCredential:success")
                    val intent = Intent(this, QuizQuestionsActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("MainActivity", "signInWithCredential:failure", task.exception)
                }
            }
    }
}



