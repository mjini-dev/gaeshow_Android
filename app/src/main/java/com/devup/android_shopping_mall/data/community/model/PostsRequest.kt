package com.devup.android_shopping_mall.data.community.model

data class PostsRequest(
    val category_id: Int,
    val page: Int,
    val limit: Int,

    val type: String?,
    val user_id: Int?,
    val search_type: String?,
    val search_word: String?
)