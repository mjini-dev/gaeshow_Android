package com.devup.android_shopping_mall.data.services.model

data class ServicesPostsRequest (
    val category_id: Int,
    val page: Int,
    val limit: Int,

    val search_type: String?,
    val search_word: String?
)