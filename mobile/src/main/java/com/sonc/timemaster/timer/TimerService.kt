package com.sonc.timemaster.timer

import com.google.firebase.database.FirebaseDatabase
import com.sonc.timemaster.TMApplication
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Martin on 5/21/17.
 */

class TimerService(var tick: Tick) {

    var running = false
    var startTs = 0L
    var endTs = 0L
    var duration: Long = 0L
        get() {
            if(running) {
                return System.currentTimeMillis() - startTs
            } else {
                return endTs - startTs
            }
        }

    var timer = Timer()

    fun start() {
        running = true
        startTs = System.currentTimeMillis()

        timer = Timer()
        timer.scheduleAtFixedRate(object: TimerTask() {
            override fun run() {
                tick.tick(duration, startTs, this@TimerService)
            }
        }, 0, 10)


    }

    fun stop() {
        timer.cancel()
        timer.purge()

        running = false
        endTs = System.currentTimeMillis()
        tick.complete(duration, startTs, endTs, this)
    }

    fun save(description: String, application: TMApplication) {
        var timerDto = TimerDTO()
        timerDto.description = description
        timerDto.duration = duration
        timerDto.createdTS = System.currentTimeMillis()

        FirebaseDatabase.getInstance()
                .getReference("timer")
                .child(application.authService.user.id)
                .push()
                .setValue(timerDto)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Timber.d("Stored successfully")
                    } else {
                        throw IllegalStateException("Unable to store timer")
                    }
                }
    }

    val format = SimpleDateFormat("mm:ss:SS")

    fun format(durationTS: Long): String {
        return format.format(durationTS)
    }
}
