package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.data.community.source.remote.PostsRemoteDataSource
import com.devup.android_shopping_mall.data.community.source.remote.PostsRemoteDataSourceImpl
import com.devup.android_shopping_mall.data.community.source.repository.PostsRepository
import com.devup.android_shopping_mall.data.community.source.repository.PostsRepositoryImpl
import org.koin.dsl.module

val postsModule = module {
    single<PostsRepository> { PostsRepositoryImpl(get()) }
    single<PostsRemoteDataSource> { PostsRemoteDataSourceImpl(get()) }
}