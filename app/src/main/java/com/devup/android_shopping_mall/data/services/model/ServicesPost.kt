package com.devup.android_shopping_mall.data.services.model

import android.text.Spanned


data class ServicesPost(
    var post_count: Int,
    var is_next: Boolean,

    val post_id: Int,
    val title: String,
    val content: String,
    var contentHTML: Spanned,
    val profile_nickname: String,
    val date: String,
    val reason: String,
    val process_status: String,

    val unique_id: Int,

    val category_id: Int,
    val attachs: List<Int>,

    var isClicked: Boolean
)
