package com.devup.android_shopping_mall.data.categories.source.local

import com.devup.android_shopping_mall.data.categories.model.Category

interface CategoriesLocalDataSource {

    fun savePostCategories(postCategories: List<Category>)

    fun getPostCategories(
        onSuccess: (postCategories: List<Category>) -> Unit,
        onFailure: () -> Unit
    )

    fun saveServiceCategories(serviceCategories: List<Category>)

    fun getServiceCategories(
        onSuccess: (serviceCategories: List<Category>) -> Unit,
        onFailure: () -> Unit
    )

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