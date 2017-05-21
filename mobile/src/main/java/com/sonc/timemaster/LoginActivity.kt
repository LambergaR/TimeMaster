package com.sonc.timemaster

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.*
import com.sonc.timemaster.auth.AuthService
import com.sonc.timemaster.timer.TimerMigrationService
import timber.log.Timber

/**
 * Created by Martin on 5/19/17.
 */
class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    val authRequestCode = 1
    var googleApiClient: GoogleApiClient? = null
    lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authService = (application as TMApplication).authService

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("381538573236-bff3smcgbee39btjj2t5fv159q0h485j.apps.googleusercontent.com")
                .requestEmail()
                .build()

        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        findViewById<SignInButton>(R.id.sign_in_button).setOnClickListener { signIn() }
    }

    fun signIn() {
        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(googleApiClient), authRequestCode)
    }

    fun handleSignInResult(result: GoogleSignInResult) {
        if(result.isSuccess) {
            // success
            firebaseAuthWithGoogle(result.signInAccount)
        } else {
            // fail
        }
    }

    @SuppressLint("WrongConstant")
    fun didLogIn(user: FirebaseUser?) {
        Toast.makeText(this, user?.email, Toast.LENGTH_LONG).show()


        if(user != null) {
            authService.user.id = user.uid
            authService.user.email = user.email

            authService.updateUser()
        }

        setResult(Activity.RESULT_OK)
        finish()
    }

    fun finishWithError() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    @SuppressLint("WrongConstant")
    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        var firebaseAuth = FirebaseAuth.getInstance()

        if(firebaseAuth.currentUser == null) {
            signInAsNewUser(credential, firebaseAuth)
        } else {
            signInAsExistingUser(credential, firebaseAuth)
        }
    }

    @SuppressLint("WrongConstant")
    fun signInAsNewUser(credential: AuthCredential, firebaseAuth: FirebaseAuth, oldUser: String? = null) {
        Timber.d("Sign in as new user")
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if(oldUser != null && firebaseAuth.currentUser != null) {
                    TimerMigrationService().migrate(firebaseAuth.currentUser!!.uid, oldUser)
                }
                didLogIn(firebaseAuth.currentUser)
            } else {
                Toast.makeText(this, "Authentication Failed: ${task.exception.toString()}", Toast.LENGTH_SHORT).show()
                finishWithError()
            }
        }
    }

    @SuppressLint("WrongConstant")
    fun signInAsExistingUser(credential: AuthCredential, firebaseAuth: FirebaseAuth) {
        Timber.d("Sign in as existing user")
        firebaseAuth.currentUser
                ?.linkWithCredential(credential)
                ?.addOnCompleteListener(this, { task ->
                    if (task.isSuccessful) {
                        didLogIn(firebaseAuth.currentUser)
                    } else {
                        if((task.exception as FirebaseAuthUserCollisionException)?.errorCode == "ERROR_CREDENTIAL_ALREADY_IN_USE") {
                            Timber.d("Merge required")
                            mergeAccounts(credential, firebaseAuth)
                        } else {
                            Toast.makeText(this, "Authentication Failed: ${task.exception.toString()}", Toast.LENGTH_SHORT).show()
                            finishWithError()
                        }
                    }
                })
    }

    fun mergeAccounts(credential: AuthCredential, firebaseAuth: FirebaseAuth) {
        Timber.d("Removing the old account")
        val id = firebaseAuth.currentUser?.uid
        authService.logout()

        // remove the old user
        authService.userDbReference.child(id).removeValue { error, _ ->
            if(error == null) {
                signInAsNewUser(credential, firebaseAuth, id)
            } else {
                throw IllegalStateException("Unable to remove the old account ${error.message}")
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == authRequestCode) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}