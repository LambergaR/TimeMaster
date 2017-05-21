package com.sonc.timemaster.timer

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Created by Martin on 5/21/17.
 */

class TimerMigrationService {
    fun migrate(newUserId: String, oldUserId: String) {
        val db = FirebaseDatabase.getInstance();
        val oldTimerRef = db.getReference("timer").child(oldUserId) ?: return

        oldTimerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                throw IllegalStateException("Unable to fetch old data")
            }

            override fun onDataChange(snapshot: DataSnapshot?) {
                var list = ArrayList<TimerDTO>()
                snapshot?.children?.forEach({ snapshot ->
                    list.add(snapshot.getValue(TimerDTO::class.java))
                })
                addAllToNew(newUserId, list)
            }

        })
    }

    private fun addAllToNew(newUserId: String, list: List<TimerDTO>) {
        var reference = FirebaseDatabase.getInstance().getReference("timer").child(newUserId)
        for(timer in list) {
            reference.setValue(timer)
        }
    }
}
