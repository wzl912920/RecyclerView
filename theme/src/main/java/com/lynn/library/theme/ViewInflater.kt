package com.lynn.library.theme

import android.app.Activity
import android.content.*
import android.content.res.TypedArray
import android.os.*
import android.support.v4.util.*
import android.support.v4.view.*
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.AppCompatCheckedTextView
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView
import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.AppCompatRatingBar
import android.support.v7.widget.AppCompatSeekBar
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.TintContextWrapper
import android.util.AttributeSet
import android.util.Log
import android.view.*
import java.lang.reflect.*

/**
 * Created by Lynn.
 */

object ViewInflater : Activity() {
    private val sConstructorSignature = arrayOf(Context::class.java , AttributeSet::class.java)
    private val sOnClickAttrs = intArrayOf(android.R.attr.onClick)

    private val sClassPrefixList = arrayOf("android.widget." , "android.view." , "android.webkit.")

    private val LOG_TAG = "ViewInflater"

    private val sConstructorMap = ArrayMap<String , Constructor<out View>>()

    private val mConstructorArgs = arrayOfNulls<Any>(2)
    fun createView(parent : View? , name : String , context : Context ,
                   attrs : AttributeSet , inheritContext : Boolean ,
                   readAndroidTheme : Boolean , readAppTheme : Boolean , wrapContext : Boolean) : View? {
        var context = context
//        val originalContext = context

        // We can emulate Lollipop's android:theme attribute propagating down the view hierarchy
        // by using the parent's context
        if (inheritContext && parent != null) {
            context = parent.context
        }
        if (readAndroidTheme || readAppTheme) {
            // We then apply the theme on the context, if specified
            context = themifyContext(context , attrs , readAndroidTheme , readAppTheme)
        }
        if (wrapContext) {
            context = TintContextWrapper.wrap(context)
        }

        var view : View? = null

        // We need to 'inject' our tint aware Views in place of the standard framework versions
        when (name) {
            "TextView" -> view = AppCompatTextView(context , attrs)
            "ImageView" -> view = AppCompatImageView(context , attrs)
            "Button" -> view = AppCompatButton(context , attrs)
            "EditText" -> view = AppCompatEditText(context , attrs)
            "Spinner" -> view = AppCompatSpinner(context , attrs)
            "ImageButton" -> view = AppCompatImageButton(context , attrs)
            "CheckBox" -> view = AppCompatCheckBox(context , attrs)
            "RadioButton" -> view = AppCompatRadioButton(context , attrs)
            "CheckedTextView" -> view = AppCompatCheckedTextView(context , attrs)
            "AutoCompleteTextView" -> view = AppCompatAutoCompleteTextView(context , attrs)
            "MultiAutoCompleteTextView" -> view = AppCompatMultiAutoCompleteTextView(context , attrs)
            "RatingBar" -> view = AppCompatRatingBar(context , attrs)
            "SeekBar" -> view = AppCompatSeekBar(context , attrs)
        }

        if (view == null/* && originalContext !== context*/) {
            // If the original context does not equal our themed context, then we need to manually
            // inflate it using the name so that android:theme takes effect.
            view = createViewFromTag(context , name , attrs)
        }

        if (view != null) {
            // If we have created a view, check its android:onClick
            checkOnClickListener(view , attrs)
        }

        return view
    }

    /**
     * Allows us to emulate the `android:theme` attribute for devices before L.
     */
    private fun themifyContext(context : Context , attrs : AttributeSet ,
                               useAndroidTheme : Boolean , useAppTheme : Boolean) : Context {
        var context = context
        val a = context.obtainStyledAttributes(attrs , android.support.v7.appcompat.R.styleable.View , 0 , 0)
        var themeId = 0
        if (useAndroidTheme) {
            // First try reading android:theme if enabled
            themeId = a.getResourceId(android.support.v7.appcompat.R.styleable.View_android_theme , 0)
        }
        if (useAppTheme && themeId == 0) {
            // ...if that didn't work, try reading app:theme (for legacy reasons) if enabled
            themeId = a.getResourceId(android.support.v7.appcompat.R.styleable.View_theme , 0)

            if (themeId != 0) {
                Log.i(LOG_TAG , "app:theme is now deprecated. " + "Please move to using android:theme instead.")
            }
        }
        a.recycle()

        if (themeId != 0 && (context !is ContextThemeWrapper || context.themeResId != themeId)) {
            // If the context isn't a ContextThemeWrapper, or it is but does not have
            // the same theme as we need, wrap it in a new wrapper
            context = ContextThemeWrapper(context , themeId)
        }
        return context
    }

    private fun createViewFromTag(context : Context , name : String , attrs : AttributeSet) : View? {
        var name = name
        if (name == "view") {
            name = attrs.getAttributeValue(null , "class")
        }
        try {
            mConstructorArgs[0] = context
            mConstructorArgs[1] = attrs

            return if (-1 == name.indexOf('.')) {
                sClassPrefixList.indices
                        .map { createView(context , name , sClassPrefixList[it]) }
                        .firstOrNull { it != null }
            } else {
                createView(context , name , null)
            }
        } catch (e : Exception) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null
        } finally {
            // Don't retain references on context.
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
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null
        }

    }

    /**
     * android:onClick doesn't handle views with a ContextWrapper context. This method
     * backports new framework functionality to traverse the Context wrappers to find a
     * suitable target.
     */
    private fun checkOnClickListener(view : View , attrs : AttributeSet) {
        val context = view.context

        if (context !is ContextWrapper || Build.VERSION.SDK_INT >= 15 && !ViewCompat.hasOnClickListeners(view)) {
            // Skip our compat functionality if: the Context isn't a ContextWrapper, or
            // the view doesn't have an OnClickListener (we can only rely on this on API 15+ so
            // always use our compat code on older devices)
            return
        }
        val a = context.obtainStyledAttributes(attrs , sOnClickAttrs)
        val handlerName = a.getString(0)
        if (handlerName != null) {
            view.setOnClickListener(DeclaredOnClickListener(view , handlerName))
        }
        a.recycle()
    }

    /**
     * An implementation of OnClickListener that attempts to lazily load a
     * named click handling method from a parent or ancestor context.
     */
    private class DeclaredOnClickListener(private val mHostView : View , private val mMethodName : String) : View.OnClickListener {

        private var mResolvedMethod : Method? = null
        private var mResolvedContext : Context? = null

        override fun onClick(v : View) {
            if (mResolvedMethod == null) {
                resolveMethod(mHostView.context , mMethodName)
            }

            try {
                mResolvedMethod!!.invoke(mResolvedContext , v)
            } catch (e : IllegalAccessException) {
                throw IllegalStateException(
                        "Could not execute non-public method for android:onClick" , e)
            } catch (e : InvocationTargetException) {
                throw IllegalStateException(
                        "Could not execute method for android:onClick" , e)
            }

        }

        private fun resolveMethod(context : Context? , name : String) {
            var context = context
            while (context != null) {
                try {
                    if (!context!!.isRestricted) {
                        val method = context!!.javaClass.getMethod(mMethodName , View::class.java)
                        if (method != null) {
                            mResolvedMethod = method
                            mResolvedContext = context
                            return
                        }
                    }
                } catch (e : NoSuchMethodException) {
                    // Failed to find method, keep searching up the hierarchy.
                }

                context = if (context is ContextWrapper) {
                    context.baseContext
                } else {
                    // Can't search up the hierarchy, null out and fail.
                    null
                }
            }

            val id = mHostView.id
            val idText = if (id == View.NO_ID)
                ""
            else
                " with id '" + mHostView.context.resources.getResourceEntryName(id) + "'"
            throw IllegalStateException(("Could not find method " + mMethodName
                    + "(View) in a parent or ancestor Context for android:onClick "
                    + "attribute defined on view " + mHostView.javaClass + idText))
        }
    }
}
