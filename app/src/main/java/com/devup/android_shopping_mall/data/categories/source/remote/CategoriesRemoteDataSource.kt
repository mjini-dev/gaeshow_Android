package com.devup.android_shopping_mall.data.categories.source.remote

import com.devup.android_shopping_mall.data.categories.model.CategoriesRequest
import com.devup.android_shopping_mall.data.categories.model.CategoriesResponse
import com.devup.android_shopping_mall.data.categories.model.Category

interface CategoriesRemoteDataSource {
    fun getCategories(
        request: CategoriesRequest,
        onSuccess: (response: List<Category>) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun getCategoriesSearch(
        request: CategoriesRequest,
        onSuccess: (response: CategoriesResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )
}