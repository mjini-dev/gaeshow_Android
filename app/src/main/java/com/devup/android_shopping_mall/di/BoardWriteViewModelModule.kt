package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.view.community.viewmodel.BoardWriteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val boardWriteViewModelModule = module {
    viewModel {
        BoardWriteViewModel(get(),get())
    }
}