package com.devup.android_shopping_mall.data.post.model

import com.devup.android_shopping_mall.data.tag.Tag

data class PostAddRequest (
    val category_id: Int,
    val title: String,
    val content: String,

    val thumbnail_id: Int?,
    val release_date: String?,
    val attach_ids: List<Int>?,
    val tags: List<Tag>?
)