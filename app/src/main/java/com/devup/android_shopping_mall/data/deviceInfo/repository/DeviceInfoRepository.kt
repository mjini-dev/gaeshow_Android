package com.devup.android_shopping_mall.data.deviceInfo.repository

import com.devup.android_shopping_mall.util.DeviceInfo

interface DeviceInfoRepository {

    fun getDeviceInfo(): DeviceInfo
}