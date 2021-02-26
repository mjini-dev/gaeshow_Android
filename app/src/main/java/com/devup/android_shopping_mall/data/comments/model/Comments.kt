package com.devup.android_shopping_mall.data.comments.model

data class Comments(
    val comment_count: Int,
    val is_next: Boolean,
    val comments:List<Comment>
)