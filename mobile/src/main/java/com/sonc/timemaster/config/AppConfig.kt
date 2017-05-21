package com.sonc.timemaster.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.sonc.timemaster.BuildConfig
import com.sonc.timemaster.R

/**
 * Created by Martin on 5/19/17.
 */
object AppConfig {
    val KEY_FONT = "font"

    val config : FirebaseRemoteConfig
        get() {
            return FirebaseRemoteConfig.getInstance()
        }

    fun AppConfig() {
        fetch()
    }

    fun inti() {
        var configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()

        config.setConfigSettings(configSettings)
        config.setDefaults(R.xml.remote_config_defaults)
    }

    fun fetch() {
        val cacheExpiration = 0L // for now
        config.fetch(cacheExpiration)?.addOnCompleteListener {
            task -> config.activateFetched()
        }
    }
}