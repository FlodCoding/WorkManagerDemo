package com.coassets.android.workmanagertest.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-08-20
 * UseDes:
 */
public class MyAccessibility extends AccessibilityService {

    private static final String TAG = "xys";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent: " + event.toString());
    }

    @Override
    public void onInterrupt() {
    }
}
