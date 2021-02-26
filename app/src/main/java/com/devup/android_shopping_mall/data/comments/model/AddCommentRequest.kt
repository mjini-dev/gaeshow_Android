package com.devup.android_shopping_mall.data.comments.model

data class AddCommentRequest(
    val depth: Int,
    val parent_id: Int?,
    val content: String
)