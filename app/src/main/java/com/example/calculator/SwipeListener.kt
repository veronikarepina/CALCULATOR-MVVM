package com.example.calculator

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.lifecycle.ViewModel

open class SwipeListener(val context: Context): OnTouchListener {
    private lateinit var gestureDetector: GestureDetector
    companion object{
        const val MIN_DISTANCE = 150
    }
    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }

    private inner class GestureListener: SimpleOnGestureListener() {
        override fun onDown(p0: MotionEvent): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val distanceX = e2.x - e1.x
            if (Math.abs(distanceX) > MIN_DISTANCE) {
                onSwipe()
                return true
            }
            return false
        }
    }
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
    open fun onSwipe(){
    }
}