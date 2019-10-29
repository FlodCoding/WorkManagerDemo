package com.coassets.android.workmanagertest.service

import android.app.Service
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
        fun onStartRecord(service: Service)
        fun onRecording(service: Service, gestureInfo: GestureInfo)
        fun onStopRecord(service: Service, gestureInfoList: ArrayList<GestureInfo>)
        fun onCancelRecord(service: Service)
    }

    interface Accessibility : Recorder {

        fun onAccessibilityEvent(service: Service, event: AccessibilityEvent)

        fun onGesturesStart(service: Service)

        fun onGesturesComplete(service: Service)
    }

    open class SimpleRecorder : Recorder {
        override fun onStartRecord(service: Service) {

        }

        override fun onRecording(service: Service, gestureInfo: GestureInfo) {
        }

        override fun onStopRecord(service: Service, gestureInfoList: ArrayList<GestureInfo>) {
        }

        override fun onCancelRecord(service: Service) {
        }


    }

    open class SimpleAccessibility : Accessibility {

        override fun onStartRecord(service: Service) {
        }

        override fun onRecording(service: Service, gestureInfo: GestureInfo) {
        }

        override fun onStopRecord(service: Service, gestureInfoList: ArrayList<GestureInfo>) {
        }

        override fun onCancelRecord(service: Service) {
        }

        override fun onAccessibilityEvent(service: Service, event: AccessibilityEvent) {
        }

        override fun onGesturesStart(service: Service) {
        }

        override fun onGesturesComplete(service: Service) {
        }
    }


}

