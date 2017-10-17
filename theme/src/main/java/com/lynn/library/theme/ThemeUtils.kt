package com.lynn.library.theme

import android.app.*
import android.content.Context
import android.support.v4.util.ArrayMap

import java.lang.reflect.Constructor
import android.support.v4.content.res.TypedArrayUtils.getResourceId
import android.content.res.TypedArray
import android.os.*
import android.support.v4.view.*
import android.support.v7.view.*
import android.support.v7.widget.*
import android.util.*
import android.view.*
import com.lynn.theme.*
import org.xmlpull.v1.*
import java.util.*


/**
 * Created by Lynn.
 */

internal object ThemeUtils {
    internal val map = WeakHashMap<Activity , LayoutInflaterFactory>()
    private val IS_PRE_LOLLIPOP = Build.VERSION.SDK_INT < 21
    internal fun createView(parent : View? , context : Activity , name : String , attrs : AttributeSet) : View? {
        var inheritContext = false
        if (IS_PRE_LOLLIPOP) {
            inheritContext = if (attrs is XmlPullParser)
                (attrs as XmlPullParser).depth > 1
            else
                shouldInheritContext(context , parent as ViewParent)// If we have a XmlPullParser, we can detect where we are in the layout
            // Otherwise we have to use the old heuristic
        }
        return try {
            ViewInflater.createView(parent , name , context , attrs , inheritContext , IS_PRE_LOLLIPOP , true , VectorEnabledTintResources.shouldBeUsed())
        } catch (e : Exception) {
            null
        } finally {
        }
    }

    private fun shouldInheritContext(context : Activity , parent : ViewParent?) : Boolean {
        if (parent == null) {
            return false
        }
        var parent : ViewParent? = null
        val windowDecor = context.window.decorView
        while (true) {
            if (parent == null) {
                // Bingo. We've hit a view which has a null parent before being terminated from
                // the loop. This is (most probably) because it's the root view in an inflation
                // call, therefore we should inherit. This works as the inflated layout is only
                // added to the hierarchy at the end of the inflate() call.
                return true
            } else if (parent === windowDecor || parent !is View
                    || ViewCompat.isAttachedToWindow(parent as View?)) {
                // We have either hit the window's decor view, a parent which isn't a View
                // (i.e. ViewRootImpl), or an attached view, so we know that the original parent
                // is currently added to the view hierarchy. This means that it has not be
                // inflated in the current inflate() call and we should not inherit the context.
                return false
            }
            parent = when (parent) {
                is View -> (parent as View).parent
                is ViewParent -> (parent as ViewParent).parent
                else -> return false
            }
        }
    }
}
