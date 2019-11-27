package com.coassets.android.workmanagertest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.LinearLayout

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-09-22
 * UseDes:
 *
 */
@SuppressLint("InflateParams")
class MovableLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr) {

    val windowLayoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            windowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }
        windowLayoutParams.format = PixelFormat.RGBA_8888
        windowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        windowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        windowLayoutParams.gravity = Gravity.END
        windowLayoutParams.y = 500
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or  WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    }


    private var tempX: Float = 0f
    private var tempY: Float = 0f
    private var isAfterMoved = false

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                tempX = event.rawX
                tempY = event.rawY
            }

            MotionEvent.ACTION_MOVE -> {
                val nowX = event.rawX
                val nowY = event.rawY
                //Gravity End
                val movedX = -(nowX - tempX)
                val movedY = nowY - tempY

                tempX = nowX
                tempY = nowY

                windowLayoutParams.x = windowLayoutParams.x + movedX.toInt()
                windowLayoutParams.y = windowLayoutParams.y + movedY.toInt()

                val windowManager =
                        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                windowManager.updateViewLayout(this, windowLayoutParams)
                isAfterMoved = true
            }
            MotionEvent.ACTION_UP -> {
                val isMoved = isAfterMoved
                isAfterMoved = false
                return isMoved
            }

        }
        return super.onInterceptTouchEvent(event)
    }


    /*@SuppressLint("RtlHardcoded")
    private fun isHorizontalEnd(): Boolean {
        val absoluteGravity = Gravity.getAbsoluteGravity(windowLayoutParams.gravity, layoutDirection)
        return (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.RIGHT

    }

    private fun isVerticalBottom(): Boolean {
        val absoluteGravity = Gravity.getAbsoluteGravity(windowLayoutParams.gravity, layoutDirection)
        return (absoluteGravity and Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM

    }*/

}