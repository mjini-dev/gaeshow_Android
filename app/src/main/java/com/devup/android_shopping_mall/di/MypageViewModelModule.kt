package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.view.mypage.MypageViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val myPageViewModelModule = module {
    viewModel {
        MypageViewModel(get(),get())
    }
}