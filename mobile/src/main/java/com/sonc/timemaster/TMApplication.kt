package com.sonc.timemaster

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.sonc.timemaster.auth.AuthService
import io.fabric.sdk.android.Fabric
import timber.log.Timber

/**
 * Created by Martin on 5/19/17.
 */
class TMApplication : Application() {

    lateinit var authService: AuthService

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.INFO)

        authService = AuthService()

        Timber.plant(Timber.DebugTree())
        Fabric.with(this, Crashlytics())
    }
}