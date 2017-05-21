package com.sonc.timemaster.timer

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import com.sonc.timemaster.R
import com.sonc.timemaster.TMApplication
import kotlinx.android.synthetic.main.activity_timer.*

/**
 * Created by Martin on 5/21/17.
 */

class TimerActivity: AppCompatActivity(), Tick {
    val timerService = TimerService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_timer)
        setSupportActionBar(toolbar)

        start.setOnClickListener {
            if(timerService.running) {
                setButtonText(R.string.start)
                timerService.stop()
            } else {
                setButtonText(R.string.stop)
                timerService.start()
            }
        }

        save.setOnClickListener {
            timerService.save("description", application as TMApplication)
            finish()
        }
    }

    fun setButtonText(@StringRes resId: Int) {
        runOnUiThread({
            start.setText(resId)
        })
    }

    fun setTime(duration: Long) {
        runOnUiThread({
            time.text = timerService.format(duration)
        })
    }

    override fun tick(duration: Long, start: Long, timerService: TimerService) {
        setTime(duration)
    }

    override fun complete(duration: Long, start: Long, end: Long, timerService: TimerService) {
        setTime(duration)
    }

}