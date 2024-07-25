package com.shubham.emergencyapplication.Utils

import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

object DraggableUtils {
    fun makeViewDraggable(view: View) {
        view.setOnTouchListener(object : OnTouchListener {
            var dX: Float = 0f
            var dY: Float = 0f
            var lastAction: Int = 0

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = v.x - event.rawX
                        dY = v.y - event.rawY
                        lastAction = MotionEvent.ACTION_DOWN
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        v.y = event.rawY + dY
                        v.x = event.rawX + dX
                        lastAction = MotionEvent.ACTION_MOVE
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            // Handle click event if needed
                            v.performClick()
                        }
                        return true
                    }

                    else -> return false
                }
            }
        })
    }
}
