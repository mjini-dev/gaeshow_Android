package com.devup.android_shopping_mall.data.post.model

data class ServicesPostAddRequest (
    val category_id: Int,
    val content: String,
    val type: Int,

    val title: String?,
    val attach_ids: List<Int>?,

    val reason: Int?,
    val unique_id: Int?
)
