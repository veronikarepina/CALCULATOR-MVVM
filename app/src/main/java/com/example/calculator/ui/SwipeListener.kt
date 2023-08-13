package com.example.calculator.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs

open class SwipeListener(context: Context): OnTouchListener {
    private var gestureDetector: GestureDetector

    companion object{
        const val SWIPE_MIN_DISTANCE = 150
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
            if (abs(distanceX) > SWIPE_MIN_DISTANCE) {
                onSwipe()
                return true
            }
            return false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    open fun onSwipe(){
    }
}