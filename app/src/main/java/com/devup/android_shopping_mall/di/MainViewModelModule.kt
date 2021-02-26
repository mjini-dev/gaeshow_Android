package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.view.home.MainViewModel
import com.devup.android_shopping_mall.view.service.ServicesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainViewModelModule = module {
    viewModel {
        MainViewModel(get(), get(), get())
    }
}