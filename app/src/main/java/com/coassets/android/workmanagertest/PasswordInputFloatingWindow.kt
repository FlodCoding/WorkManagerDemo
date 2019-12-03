package com.coassets.android.workmanagertest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import kotlinx.android.synthetic.main.dialog_password_input.view.*

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-10-30
 * UseDes:
 */
@SuppressLint("InflateParams")
class PasswordInputFloatingWindow(context: Context, onPasswordInputListener: OnPasswordInputListener) {

    private val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val windowLayoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
    private val rootView: View

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            windowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            windowLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        windowLayoutParams.format = PixelFormat.RGBA_8888
        windowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        windowLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        windowLayoutParams.gravity = Gravity.CENTER
        windowLayoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_password_input, null)

        val fabDone = rootView.fabDone
        val etInput = rootView.etInput

        /*rootView.container.setOnClickListener {
            dismiss()
            onPasswordInputListener.onCancel()
        }*/

        rootView.addOnUnhandledKeyEventListener { v, event ->
            if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss()
                onPasswordInputListener.onCancel()
            }
            false
        }

        fabDone.setOnClickListener {
            onPasswordInputListener.onSubmit(etInput.text.toString())
            dismiss()
        }



        etInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s ?: return

                if (s.length > 3) {
                    fabDone.show()
                } else {
                    fabDone.hide()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }


    fun show() {
        wm.addView(rootView, windowLayoutParams)
    }

    fun dismiss() {
        wm.removeView(rootView)
    }


    interface OnPasswordInputListener {
        fun onSubmit(password: String)
        fun onCancel()
    }

}
