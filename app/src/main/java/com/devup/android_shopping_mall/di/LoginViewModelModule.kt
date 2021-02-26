package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.view.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val loginViewModelModule = module {
    viewModel {
        LoginViewModel(get(), get(), get(), get())
    }
}