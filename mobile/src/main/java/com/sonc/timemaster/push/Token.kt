package com.sonc.timemaster.push

/**
 * Created by Martin on 5/21/17.
 */

class Token(var token: String?) {
    var initialized: Boolean = false
        get() {
            return token != null && token != ""
        }

}