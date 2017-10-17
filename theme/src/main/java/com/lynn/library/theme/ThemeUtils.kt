package com.lynn.library.theme

import android.content.Context
import android.support.v4.util.ArrayMap
import android.util.AttributeSet
import android.view.InflateException
import android.view.View

import java.lang.reflect.Constructor

/**
 * Created by Lynn.
 */

internal object ThemeUtils {

    private val mConstructorArgs = arrayOfNulls<Any>(2)
    private val sConstructorSignature = arrayOf(Context::class.java , AttributeSet::class.java)

    private val sClassPrefixList = arrayOf("android.widget." , "android.view." , "android.webkit.")

    private val sConstructorMap = ArrayMap<String , Constructor<out View>>()
    internal fun createView(context : Context , name : String , attrs : AttributeSet) : View? {
        try {
            mConstructorArgs[0] = context
            mConstructorArgs[1] = attrs
            if (-1 == name.indexOf('.')) {
                for (i in sClassPrefixList.indices) {
                    val view = createView(context , name , sClassPrefixList[i])
                    if (view != null) {
                        return view
                    }
                }
                return null
            } else {
                return createView(context , name , null)
            }
        } catch (e : Exception) {
            return null
        } finally {
            mConstructorArgs[0] = null
            mConstructorArgs[1] = null
        }
    }

    @Throws(ClassNotFoundException::class , InflateException::class)
    private fun createView(context : Context , name : String , prefix : String?) : View? {
        var constructor : Constructor<out View>? = sConstructorMap[name]
        try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                val clazz = context.classLoader.loadClass(
                        if (prefix != null) prefix + name else name).asSubclass(View::class.java)

                constructor = clazz.getConstructor(*sConstructorSignature)
                sConstructorMap.put(name , constructor)
            }
            constructor!!.isAccessible = true
            return constructor.newInstance(*mConstructorArgs)
        } catch (e : Exception) {
            return null
        }

    }
}
