package com.devup.android_shopping_mall.data.categories.model

data class CategoriesRequest(
    val type: String,
    val search_word: String?,
    val page: Int?,
    val limit: Int?
)