package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.data.deviceInfo.local.DeviceInfoLocalDataSource
import com.devup.android_shopping_mall.data.deviceInfo.local.DeviceInfoLocalDataSourceImpl
import com.devup.android_shopping_mall.data.deviceInfo.repository.DeviceInfoRepository
import com.devup.android_shopping_mall.data.deviceInfo.repository.DeviceInfoRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val deviceInfoModule = module {
    single<DeviceInfoRepository> { DeviceInfoRepositoryImpl(get())}
    single<DeviceInfoLocalDataSource> { DeviceInfoLocalDataSourceImpl(androidContext())}
}