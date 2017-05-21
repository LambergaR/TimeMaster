package com.sonc.timemaster.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import timber.log.Timber


/**
 * Created by Martin on 5/21/17.
 */

class AuthService {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var userDbReference: DatabaseReference

    lateinit var user: User

    constructor() {
        userDbReference = database.getReference("user")

        if(isLoggedIn()) {
            loadUser()
        } else {
            loginAnonymous()
        }
    }

    fun check() {
        if(!isLoggedIn()) {
            loginAnonymous()
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    fun isLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun loadUser() {
        val u = firebaseAuth.currentUser
        if(u != null) {
            userDbReference.child(u.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    var _user = dataSnapshot?.getValue(User::class.java)

                    if (_user == null) {
                        Timber.d("User not found")
                        loginAnonymous()
                    } else {
                        Timber.d("Successfully loaded user")
                        user = _user
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    throw IllegalStateException("Unable to load user ${databaseError.message}")
                }
            })
        }
    }

    fun loginAnonymous() {
        firebaseAuth.signInAnonymously().addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Timber.d("Logged in anonymously")

                val u = firebaseAuth.currentUser
                if (u != null) {
                    var user = User()
                    user.id = u.uid

                    createUser(user)
                }
            } else {
                throw IllegalStateException("Unable to login anonymously ${task.exception.toString()}")
            }
        }
    }

    fun updateUser() {
        userDbReference.child(user.id).setValue(user).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Timber.d("Update successful")
            } else {
                throw IllegalStateException("Unable to update user ${task.exception.toString()}")
            }

        }
    }

    fun createUser(user: User) {
        userDbReference.child(user.id)
                .setValue(user)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        this.user = user
                        Timber.d("User created")
                    } else {
                        throw IllegalStateException("Unable to create user")
                    }
                }

    }

}
