package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.view.mypage.MoreFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val moreFragmentViewModelModule = module {
    viewModel {
        MoreFragmentViewModel(get(), get(), get(), get(), get())
    }
}