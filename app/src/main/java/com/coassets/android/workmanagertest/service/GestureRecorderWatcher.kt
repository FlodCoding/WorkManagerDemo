package com.coassets.android.workmanagertest.service

import com.flod.view.GestureInfo

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-10-09
 * UseDes: 反馈给外部，或者客户端的接口
 *
 */

class GestureRecorderWatcher {

    interface Listener {
        fun onStartRecord()
        fun onRecording(gestureInfo: GestureInfo)
        fun onStopRecord(gestureInfoList: ArrayList<GestureInfo>)
        fun onCancelRecord()
    }

    open class SimpleListener : Listener {
        override fun onStartRecord() {

        }

        override fun onRecording(gestureInfo: GestureInfo) {
        }

        override fun onStopRecord(gestureInfoList: ArrayList<GestureInfo>) {
        }

        override fun onCancelRecord() {
        }
    }


}

