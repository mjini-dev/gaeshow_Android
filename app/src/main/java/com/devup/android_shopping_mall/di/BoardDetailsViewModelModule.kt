package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.view.community.viewmodel.BoardDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val boardDetailsViewModelModule = module {
    viewModel {
        BoardDetailsViewModel(get(), get(), get(), get(), get(), get())
    }
}