package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.data.post.source.remote.PostRemoteDataSource
import com.devup.android_shopping_mall.data.post.source.remote.PostRemoteDataSourceImpl
import com.devup.android_shopping_mall.data.post.source.repository.PostRepository
import com.devup.android_shopping_mall.data.post.source.repository.PostRepositoryImpl
import org.koin.dsl.module

val postModule = module {
    single<PostRepository> { PostRepositoryImpl(get()) }
    single<PostRemoteDataSource> { PostRemoteDataSourceImpl(get()) }
}