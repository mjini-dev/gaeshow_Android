package com.devup.android_shopping_mall.data.categories.model


/**
 * Categories 데이터클래스
 */
data class CategoriesResponse(
    var category_count: Int?,
    var is_next: Boolean?,
    val categories: List<Category>
)