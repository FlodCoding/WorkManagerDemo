@file:Suppress("unused", "SpellCheckingInspection")

package com.coassets.android.workmanagertest.utils

import android.os.Parcel
import android.os.Parcelable


/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-09-18
 * UseDes:
 *
 */
object ParcelableUtil {

    fun marshall(parcelable: Parcelable): ByteArray {
        val parcel = Parcel.obtain()
        parcelable.writeToParcel(parcel, 0)
        val byteArray = parcel.marshall()
        parcel.recycle()
        return byteArray
    }

    fun unmarshall(byteArray: ByteArray): Parcel {
        val parcel = Parcel.obtain()
        parcel.unmarshall(byteArray, 0, byteArray.size)
        parcel.setDataPosition(0)
        return parcel
    }

    fun <T> unmarshall(byteArray: ByteArray, creator: Parcelable.Creator<T>): T {
        val parcel = unmarshall(byteArray)
        val result = creator.createFromParcel(parcel)
        parcel.recycle()

        return result
    }
}