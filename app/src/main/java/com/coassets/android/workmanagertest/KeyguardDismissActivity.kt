package com.coassets.android.workmanagertest

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class KeyguardDismissActivity : AppCompatActivity() {


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_result)
    }

    override fun onRestart() {
        super.onRestart()
        finish()
    }


}
