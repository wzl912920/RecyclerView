package com.lynn.library.theme

import android.content.*
import android.content.res.*
import org.xmlpull.v1.*
import java.io.*
import java.lang.reflect.*


/**
 * Created by Lynn.
 */

internal object AssertUtils {
    @Throws(IllegalAccessException::class , InstantiationException::class , IllegalArgumentException::class , SecurityException::class , InvocationTargetException::class , NoSuchMethodException::class , IOException::class , XmlPullParserException::class)
    internal fun getPlugInResource(context : Context , apkPath : String , pkgWrapper : PKGWrapper? = null) : Resources {
        val rl : Resources
        val path = File(apkPath)
        if (!path.isFile || ThemeConfig.getInstance().isDefaultTheme()) {
            pkgWrapper?.pkgName = context.packageName
            return context.resources
        }

        val am = AssetManager::class.java.newInstance() as AssetManager
        am.javaClass.getMethod("addAssetPath" , String::class.java)
                .invoke(am , path.absolutePath)

        var packageName : String? = null

        val xml = am.openXmlResourceParser("AndroidManifest.xml")
        var eventType = xml.eventType
        xmlloop@ while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> if ("manifest" == xml.name) {
                    packageName = xml.getAttributeValue(null , "package")
                    break@xmlloop
                }
            }
            eventType = xml.nextToken()
        }
        xml.close()
        if (packageName == null || packageName.isEmpty()) {
            packageName = context.packageName
        }
        pkgWrapper?.pkgName = packageName!!
        val superRes = context.resources
        rl = Resources(am , superRes.displayMetrics ,
                       superRes.configuration)
        return rl
    }

    internal class PKGWrapper {
        var pkgName : String = ""
    }
}
