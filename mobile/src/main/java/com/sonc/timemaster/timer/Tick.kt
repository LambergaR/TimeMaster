package com.sonc.timemaster.timer

/**
 * Created by Martin on 5/21/17.
 */
interface Tick {
    fun tick(duration: Long, start: Long, timerService: TimerService)
    fun complete(duration: Long, start: Long, end: Long, timerService: TimerService)
}