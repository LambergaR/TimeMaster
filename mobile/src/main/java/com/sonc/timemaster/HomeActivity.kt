package com.sonc.timemaster

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.sonc.timemaster.auth.AuthService
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    val loginRequestCode = 12321
    lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        authService = (application as TMApplication).authService

        initViews()
    }

    override fun onResume() {
        super.onResume()

        checkPlayServices()
        authService.check()
    }

    fun initViews() {
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        button.setOnClickListener {
            if(authService.user.email == null) {
                login()
            } else {
                logout()
            }
        }
    }

    fun checkPlayServices() {
        var googleApiAvailability = GoogleApiAvailability.getInstance()

        if(googleApiAvailability.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            googleApiAvailability.makeGooglePlayServicesAvailable(this)
        }
    }

    fun logout() {
        authService.logout()
        finish()
    }

    fun login() {
        startActivityForResult(Intent(this, LoginActivity::class.java), loginRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == loginRequestCode) {
            if(resultCode == Activity.RESULT_OK) {
                // login success
            } else {
                // login fail
            }
        }
    }
}