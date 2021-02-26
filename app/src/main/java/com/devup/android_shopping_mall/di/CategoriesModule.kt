package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.data.categories.source.local.CategoriesLocalDataSource
import com.devup.android_shopping_mall.data.categories.source.local.CategoriesLocalDataSourceImpl
import com.devup.android_shopping_mall.data.categories.source.remote.CategoriesRemoteDataSource
import com.devup.android_shopping_mall.data.categories.source.remote.CategoriesRemoteDataSourceImpl
import com.devup.android_shopping_mall.data.categories.source.repository.CategoriesRepository
import com.devup.android_shopping_mall.data.categories.source.repository.CategoriesRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val categoriesModule = module {
    single<CategoriesRepository> { CategoriesRepositoryImpl(get(),get()) }
    single<CategoriesLocalDataSource> { CategoriesLocalDataSourceImpl(androidContext()) }
    single<CategoriesRemoteDataSource> { CategoriesRemoteDataSourceImpl(get()) }
}