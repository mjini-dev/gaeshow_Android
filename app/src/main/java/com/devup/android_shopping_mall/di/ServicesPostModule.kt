package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.data.services.source.remote.ServicesPostsRemoteDataSource
import com.devup.android_shopping_mall.data.services.source.remote.ServicesPostsRemoteDataSourceImpl
import com.devup.android_shopping_mall.data.services.source.repository.ServicesPostsRepository
import com.devup.android_shopping_mall.data.services.source.repository.ServicesPostsRepositoryImpl
import org.koin.dsl.module

val servicesPostModule = module {
    single<ServicesPostsRepository> { ServicesPostsRepositoryImpl(get()) }
    single<ServicesPostsRemoteDataSource> { ServicesPostsRemoteDataSourceImpl(get()) }
}