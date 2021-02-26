package com.devup.android_shopping_mall.di

import com.devup.android_shopping_mall.view.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchViewModelModule = module {
    viewModel {
        SearchViewModel(get())
    }
}