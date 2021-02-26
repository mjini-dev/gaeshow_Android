package com.devup.android_shopping_mall.data.categories.source.repository

import com.devup.android_shopping_mall.data.categories.model.CategoriesRequest
import com.devup.android_shopping_mall.data.categories.model.CategoriesResponse
import com.devup.android_shopping_mall.data.categories.model.Category
import com.devup.android_shopping_mall.data.categories.source.local.CategoriesLocalDataSource
import com.devup.android_shopping_mall.data.categories.source.remote.CategoriesRemoteDataSource

class CategoriesRepositoryImpl(
    private val categoriesLocalDataSource: CategoriesLocalDataSource,
    private val categoriesRemoteDataSource: CategoriesRemoteDataSource
) : CategoriesRepository {

    override fun getCategories(
        request: CategoriesRequest,
        onSuccess: (response: List<Category>) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        categoriesRemoteDataSource.getCategories(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun getCategoriesSearch(
        request: CategoriesRequest,
        onSuccess: (response: CategoriesResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        categoriesRemoteDataSource.getCategoriesSearch(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun saveStoreCategories(storeCategories: List<Category>) {

    }

    override fun savePostCategories(postCategories: List<Category>) {
        categoriesLocalDataSource.savePostCategories(postCategories)
    }

    override fun getPostCategories(
        request: CategoriesRequest,
        onSuccess: (response: List<Category>) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        categoriesLocalDataSource.getPostCategories(
            onSuccess,
            onFailure = { categoriesRemoteDataSource.getCategories(request, onSuccess, notSuccessStatus, onFailure) }
        )
    }

    override fun saveServiceCategories(serviceCategories: List<Category>) {
        categoriesLocalDataSource.saveServiceCategories(serviceCategories)
    }

    override fun saveJobCategories(jobCategories: List<Category>) {
        categoriesLocalDataSource.saveJobCategories(jobCategories)
    }

    override fun getJobCategories(onSuccess: (jobCategories: List<Category>) -> Unit, onFailure: () -> Unit) {
        categoriesLocalDataSource.getJobCategories(onSuccess, onFailure)
    }

    override fun saveIdeCategories(ideCategories: List<Category>) {
        categoriesLocalDataSource.saveIdeCategories(ideCategories)
    }

    override fun getIdeCategories(onSuccess: (ideCategories: List<Category>) -> Unit, onFailure: () -> Unit) {
        categoriesLocalDataSource.getIdeCategories(onSuccess, onFailure)
    }

    override fun saveLanguageCategories(languageCategories: List<Category>) {
        categoriesLocalDataSource.saveLanguageCategories(languageCategories)
    }

    override fun getLanguageCategories(onSuccess: (languageCategories: List<Category>) -> Unit, onFailure: () -> Unit) {
        categoriesLocalDataSource.getLanguageCategories(onSuccess, onFailure)
    }
}