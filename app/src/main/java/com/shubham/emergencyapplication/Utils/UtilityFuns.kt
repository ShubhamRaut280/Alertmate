package com.shubham.emergencyapplication.Utils

import android.graphics.Rect
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.shubham.emergencyapplication.R

object UtilityFuns {
    fun handleAdjustResizeForKeyboard(rootView: RelativeLayout) {
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val visibleHeight = rect.bottom - rect.top
            val heightDiff = screenHeight - visibleHeight

            // Adjust threshold as needed
            val threshold = 100

            // Assuming keyboard is open if heightDiff is significant
            val keyboardHeight = if (heightDiff > threshold) heightDiff else heightDiff/2

            // Directly set padding based on keyboard height
            rootView.setPadding(
                rootView.paddingLeft,
                rootView.paddingTop,
                rootView.paddingRight,
                keyboardHeight - threshold/2
            )
        }

    }
    fun handleAdjustResizeForKeyboard(rootView: ConstraintLayout) {
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val visibleHeight = rect.bottom - rect.top
            val heightDiff = screenHeight - visibleHeight

            // Adjust threshold as needed
            val threshold = 100

            // Assuming keyboard is open if heightDiff is significant
            val keyboardHeight = if (heightDiff > threshold) heightDiff else heightDiff/2

            // Directly set padding based on keyboard height
            rootView.setPadding(
                rootView.paddingLeft,
                rootView.paddingTop,
                rootView.paddingRight,
                keyboardHeight - threshold
            )
            val color  = rootView.resources.getColor(R.color.main)
            rootView.setBackgroundColor(color)
        }
    }
}