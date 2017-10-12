package com.lynn.simplerecyclerview.serviceExample

import android.app.*
import android.content.*
import android.os.*
import java.lang.ref.*

/**
 * Created by Lynn.
 */

class TestService : Service() {

    override fun onBind(intent : Intent?) : IBinder? {
        return MyBinder(this)
    }

    private fun sendBroadCast() {
        val i = Intent(this , TestBroadCast::class.java)
        val bd = Bundle()
        bd.putString("A" , "Hahahaahahahaahh")
        val obj = TestObject("-=-=-=-=-=-=-=-=-=-=-")
        bd.putParcelable("B" , obj)
        val p = Parcel.obtain()
        p.setDataPosition(0)
        obj.writeToParcel(p , 0)
        val t = p.marshall()
        p.recycle()
        bd.putByteArray("C" , t)
        bd.putInt("D" , 10)
        i.putExtra("E" , bd)

        val pi = PendingIntent.getBroadcast(baseContext , Math.random().toInt() , i , PendingIntent.FLAG_ONE_SHOT)
        val m = baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        /**
         * AlarmManager.RTC，硬件闹钟，不唤醒手机（也可能是其它设备）休眠；当手机休眠时不发射闹钟。
         * AlarmManager.RTC_WAKEUP，硬件闹钟，当闹钟发躰时唤醒手机休眠；
         * AlarmManager.ELAPSED_REALTIME，真实时间流逝闹钟，不唤醒手机休眠；当手机休眠时不发射闹钟。
         * AlarmManager.ELAPSED_REALTIME_WAKEUP，真实时间流逝闹钟，当闹钟发躰时唤醒手机休眠；
         * RTC闹钟和ELAPSED_REALTIME最大的差别就是前者可以通过修改手机时间触发闹钟事件，后者要通过真实时间的流逝，即使在休眠状态，时间也会被计算。
         */
        m.set(AlarmManager.RTC_WAKEUP , 0 , pi)
    }

    companion object {
        class MyBinder(service : TestService) : Binder() {
            private val sr = SoftReference(service)
            fun getService() {
                sr?.get()?.sendBroadCast()
            }
        }

        class TestObject() : Parcelable {
            var s : String? = ""

            constructor(str : String) : this() {
                this.s = str
            }

            constructor(parcel : Parcel) : this() {
                s = parcel.readString()
            }

            override fun writeToParcel(parcel : Parcel , flags : Int) {
                parcel.writeString(s ?: "")
            }

            override fun describeContents() : Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<TestObject> {
                override fun createFromParcel(parcel : Parcel) : TestObject {
                    return TestObject(parcel)
                }

                override fun newArray(size : Int) : Array<TestObject?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}
