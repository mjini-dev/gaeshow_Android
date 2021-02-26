package com.devup.android_shopping_mall.util

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.os.Build
import android.provider.Settings
import android.content.Context

class DeviceInfo(val context: Context) {

    // android device id
    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    // android devcie model
    fun getDeviceModel(): String {
        return Build.MODEL
    }

    // android devcie os
    fun getDeviceOs(): String {
        //return Build.VERSION.RELEASE.toString()
        return "A"
    }

    // android app version
    fun getAppVersion(): String {
        val info: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return info.versionName
    }

}