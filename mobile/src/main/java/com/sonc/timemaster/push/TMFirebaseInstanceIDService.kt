package com.sonc.timemaster.push

import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Created by Martin on 5/19/17.
 */

class TMFirebaseInstanceIDService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
    }
}