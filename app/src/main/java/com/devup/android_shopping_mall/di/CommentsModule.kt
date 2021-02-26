package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.data.source.remote.CommentsRemoteDataSource
import com.devup.android_shopping_mall.data.comments.source.remote.CommentsRemoteDataSourceImpl
import com.devup.android_shopping_mall.data.comments.source.repository.CommentsRepository
import com.devup.android_shopping_mall.data.comments.source.repository.CommentsRepositoryImpl
import org.koin.dsl.module

val commentsModule = module {
    single<CommentsRepository> { CommentsRepositoryImpl(get()) }
    single<CommentsRemoteDataSource> { CommentsRemoteDataSourceImpl(get()) }
}