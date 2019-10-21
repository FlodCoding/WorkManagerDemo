package com.coassets.android.workmanagertest.data

import android.os.Parcel
import android.os.Parcelable
import com.coassets.android.workmanagertest.utils.ParcelableUtil
import com.flod.view.GestureInfo

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-10-02
 * UseDes:
 *
 */

class GestureBundle(
        val gestureInfoList: ArrayList<GestureInfo>) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(GestureInfo.CREATOR)!!)


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(gestureInfoList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GestureBundle> {
        override fun createFromParcel(parcel: Parcel): GestureBundle {
            return GestureBundle(parcel)
        }

        override fun newArray(size: Int): Array<GestureBundle?> {
            return arrayOfNulls(size)
        }

        fun fromBytes(byteArray: ByteArray): GestureBundle {
            return ParcelableUtil.unmarshall(byteArray, CREATOR)
        }
    }



    fun toBytes(): ByteArray {

        return ParcelableUtil.marshall(this)
    }




}

