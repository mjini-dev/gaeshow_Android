package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.view.community.viewmodel.BoardViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val boardViewModelModule = module {
    viewModel {
        BoardViewModel(get(), get(), get())
    }
}