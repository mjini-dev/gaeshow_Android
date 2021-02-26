package com.devup.android_shopping_mall.data.deviceInfo.local

import android.content.Context
import com.devup.android_shopping_mall.util.DeviceInfo

class DeviceInfoLocalDataSourceImpl(context: Context) : DeviceInfoLocalDataSource {

    private val deviceInfo = DeviceInfo(context)

    override fun getDeviceInfo(): DeviceInfo {
        return deviceInfo
    }
}