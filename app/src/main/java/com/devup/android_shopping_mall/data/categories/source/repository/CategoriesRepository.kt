package com.devup.android_shopping_mall.data.categories.source.repository

import com.devup.android_shopping_mall.data.categories.model.CategoriesRequest
import com.devup.android_shopping_mall.data.categories.model.CategoriesResponse
import com.devup.android_shopping_mall.data.categories.model.Category

interface CategoriesRepository {

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

    fun saveStoreCategories(storeCategories: List<Category>)

    fun savePostCategories(postCategories: List<Category>)
    fun getPostCategories(
        request: CategoriesRequest,
        onSuccess: (response: List<Category>) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun saveServiceCategories(serviceCategories: List<Category>)

    fun saveJobCategories(jobCategories: List<Category>)

    fun getJobCategories(
        onSuccess: (jobCategories: List<Category>) -> Unit,
        onFailure: () -> Unit
    )


    fun saveIdeCategories(ideCategories: List<Category>)

    fun getIdeCategories(
        onSuccess: (ideCategories: List<Category>) -> Unit,
        onFailure: () -> Unit
    )

    fun saveLanguageCategories(languageCategories: List<Category>)

    fun getLanguageCategories(
        onSuccess: (languageCategories: List<Category>) -> Unit,
        onFailure: () -> Unit
    )

}