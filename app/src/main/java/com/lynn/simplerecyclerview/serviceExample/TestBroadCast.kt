package com.lynn.simplerecyclerview.serviceExample

import android.content.*
import com.lynn.library.util.*
import android.os.Parcel


/**
 * Created by Lynn.
 */

class TestBroadCast : BroadcastReceiver() {
    override fun onReceive(context : Context? , intent : Intent?) {
        log("BroadcastReceiver : onReceive")
        val extras = intent?.getBundleExtra("E")
        val a = extras?.getString("A")
        val b = extras?.getParcelable<TestService.Companion.TestObject>("B")
        val obj = extras?.getByteArray("C")
        var c : TestService.Companion.TestObject? = null
        obj?.apply {
            val parcel = Parcel.obtain()
            parcel.setDataPosition(0)
            parcel.unmarshall(obj , 0 , obj.size)
            parcel.setDataPosition(0) // This is
            c = TestService.Companion.TestObject.createFromParcel(parcel)
            parcel.recycle()
        }
        val d = extras?.getInt("D")
        context?.showWarning("receive $a \n ${b?.s} \n ${c?.s} $d")
    }
}