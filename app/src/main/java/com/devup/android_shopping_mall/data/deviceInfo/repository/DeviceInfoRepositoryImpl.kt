package com.devup.android_shopping_mall.data.deviceInfo.repository

import com.devup.android_shopping_mall.util.DeviceInfo
import com.devup.android_shopping_mall.data.deviceInfo.local.DeviceInfoLocalDataSource

class DeviceInfoRepositoryImpl(
    private val deviceInfoLocalDataSource: DeviceInfoLocalDataSource
) : DeviceInfoRepository {
    override fun getDeviceInfo(): DeviceInfo {
        return deviceInfoLocalDataSource.getDeviceInfo()
    }
}