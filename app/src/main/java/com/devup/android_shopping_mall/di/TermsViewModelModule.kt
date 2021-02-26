package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.view.terms.TermsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val termsViewModelModule = module {
    viewModel {
        TermsViewModel(get(),get(),get())
    }
}