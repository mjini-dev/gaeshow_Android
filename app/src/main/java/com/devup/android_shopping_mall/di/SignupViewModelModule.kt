package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.view.join.SignupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val signupViewModelNodule = module {
    viewModel {
        SignupViewModel(get(),get(),get(),get(),get())
    }
}