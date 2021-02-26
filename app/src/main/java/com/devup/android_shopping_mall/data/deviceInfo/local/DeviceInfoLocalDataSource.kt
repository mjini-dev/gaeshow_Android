package com.devup.android_shopping_mall.data.deviceInfo.local

import com.devup.android_shopping_mall.util.DeviceInfo

interface DeviceInfoLocalDataSource {

    fun getDeviceInfo(): DeviceInfo
}