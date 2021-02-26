package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.data.notification.source.remote.NotificationRemoteDataSource
import com.devup.android_shopping_mall.data.notification.source.remote.NotificationRemoteDataSourceImpl
import com.devup.android_shopping_mall.data.notification.source.repository.NotificationRepository
import com.devup.android_shopping_mall.data.notification.source.repository.NotofocationRepositoryImpl
import org.koin.dsl.module

val notificationModule = module {
    single<NotificationRepository> { NotofocationRepositoryImpl(get()) }
    single<NotificationRemoteDataSource> { NotificationRemoteDataSourceImpl(get()) }
}