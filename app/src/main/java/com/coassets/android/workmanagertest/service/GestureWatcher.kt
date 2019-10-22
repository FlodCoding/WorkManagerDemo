package com.coassets.android.workmanagertest.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.flod.gesture.GestureInfo


/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-10-09
 * UseDes: 反馈给外部，或者客户端的接口
 *
 */

class GestureWatcher {

    interface Recorder {
        fun onStartRecord()
        fun onRecording(gestureInfo: GestureInfo)
        fun onStopRecord(gestureInfoList: ArrayList<GestureInfo>)
        fun onCancelRecord()
    }

    interface Accessibility {
        fun onStartRecord(service: AccessibilityService)
        fun onRecording(service: AccessibilityService, gestureInfo: GestureInfo)
        fun onStopRecord(service: AccessibilityService, gestureInfoList: ArrayList<GestureInfo>)
        fun onCancelRecord(service: AccessibilityService)

        fun onAccessibilityEvent(service: AccessibilityService, event: AccessibilityEvent)
    }

    open class SimpleRecorder : Recorder {
        override fun onStartRecord() {

        }

        override fun onRecording(gestureInfo: GestureInfo) {
        }

        override fun onStopRecord(gestureInfoList: ArrayList<GestureInfo>) {
        }

        override fun onCancelRecord() {
        }
    }

    open class SimpleAccessibility : Accessibility {

        override fun onAccessibilityEvent(service: AccessibilityService, event: AccessibilityEvent) {
        }

        override fun onStartRecord(service: AccessibilityService) {
        }

        override fun onRecording(service: AccessibilityService, gestureInfo: GestureInfo) {
        }

        override fun onStopRecord(
            service: AccessibilityService,
            gestureInfoList: ArrayList<GestureInfo>
        ) {
        }

        override fun onCancelRecord(service: AccessibilityService) {
        }

    }


}

