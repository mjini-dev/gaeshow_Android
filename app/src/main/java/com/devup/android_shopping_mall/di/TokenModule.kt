package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.data.user.source.local.TokenLocalDataSource
import com.devup.android_shopping_mall.data.user.source.local.TokenLocalDataSourceImpl
import com.devup.android_shopping_mall.data.user.source.repository.TokenRepository
import com.devup.android_shopping_mall.data.user.source.repository.TokenRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val tokenModule = module {
    single<TokenRepository> { TokenRepositoryImpl(get()) }
    single<TokenLocalDataSource> { TokenLocalDataSourceImpl(androidContext()) }
}