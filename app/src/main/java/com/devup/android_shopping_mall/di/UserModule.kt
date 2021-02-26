package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.data.user.source.local.UserLocalDataSource
import com.devup.android_shopping_mall.data.user.source.local.UserLocalDataSourceImpl
import com.devup.android_shopping_mall.data.user.source.remote.UserRemoteDataSource
import com.devup.android_shopping_mall.data.user.source.remote.UserRemoteDataSourceImpl
import com.devup.android_shopping_mall.data.user.source.repository.UserRepository
import com.devup.android_shopping_mall.data.user.source.repository.UserRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val userModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<UserLocalDataSource> { UserLocalDataSourceImpl(androidContext()) }
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(get()) }
}