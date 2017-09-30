package com.lynn.library.permission

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.DialogInterface.OnClickListener
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity

import java.util.ArrayList

open class PermissionsActivity : AppCompatActivity() , OnRequestPermissionsResultCallback {
    private val list = mutableListOf<String>()
    private val deniedList = mutableListOf<String>()
    private var permissionType : Int = 0
    private val REQUEST_CODE_REQUEST_PERMISSION = 0x9901

    protected fun askPermission(vararg permissions : String) {
        this.askPermission(0 , *permissions)
    }

    protected fun askPermission(type : Int , vararg permissions : String) {
        this.permissionType = type
        this.filterPermission(*permissions)
        if (this.list != null && !this.list.isEmpty()) {
            val array = list.toTypedArray()
            ActivityCompat.requestPermissions(this , array , this.REQUEST_CODE_REQUEST_PERMISSION)
        } else {
            this.onPermissionGranted(this.permissionType)
        }
    }

    private fun filterPermission(vararg permissions : String) {
        this.list.clear()
        this.deniedList.clear()
        for (i in permissions.indices) {
            val p = permissions[i]
            if (!hasPermissions(p)) {
                this.list.add(p)
            }
        }
    }

    protected open fun onPermissionGranted(type : Int) {}

    protected open fun onPermissionDenied(type : Int , permissions : MutableList<String>) {}

    protected open fun dealSelf() : Boolean {
        return false
    }

    protected fun hasPermissions(vararg permissions : String) : Boolean {
        var hasPermission = true
        for (i in permissions.indices) {
            hasPermission = ContextCompat.checkSelfPermission(this , permissions[i]) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                return hasPermission
            }
        }
        return hasPermission
    }

    private fun startSettings() {
        var i : Intent
        if (this.deniedList.contains(Manifest.permission.BIND_ACCESSIBILITY_SERVICE)) {
            i = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            this.startActivity(i)
            if (deniedList.size == 1) {
                return
            }
        }
        i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        i.data = Uri.fromParts("package" , this.packageName , null)
        this.startActivity(i)
    }

    override fun onRequestPermissionsResult(requestCode : Int , permissions : Array<String> , grantResults : IntArray) {
        super.onRequestPermissionsResult(requestCode , permissions , grantResults)
        if (requestCode == REQUEST_CODE_REQUEST_PERMISSION) {
            if (permissions.size != grantResults.size) return
            for (i in permissions.indices) {
                val p = permissions[i]
                val grants = grantResults[i]
                list.remove(p)
                if (grants == PackageManager.PERMISSION_DENIED) {
                    deniedList.add(p)
                }
            }
            if (null == list || list.size == 0) {
                if (deniedList == null || deniedList.size == 0) {
                    onPermissionGranted(permissionType)
                } else {
                    if (dealSelf()) {
                        onPermissionDenied(permissionType , deniedList)
                    } else {
                        AlertDialog.Builder(this)
                                .setTitle("权限缺失")
                                .setMessage("部分权限未开启，请开启权限后再试")
                                .setNegativeButton("取消") { dialog , which -> dialog.dismiss() }
                                .setPositiveButton("设置") { dialog , which ->
                                    dialog.dismiss()
                                    startSettings()
                                }
                                .create().show()
                    }
                }
            }
        }
    }
}
