package com.devup.android_shopping_mall.data.post.model

data class PostRatingsAddRequest (
    val type: String,
    val unique_id: Int,
    val rating: Int
)