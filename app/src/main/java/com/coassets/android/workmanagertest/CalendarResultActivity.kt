package com.coassets.android.workmanagertest

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class CalendarResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_result)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )
    }

    override fun onRestart() {
        super.onRestart()
        finish()
    }


}
