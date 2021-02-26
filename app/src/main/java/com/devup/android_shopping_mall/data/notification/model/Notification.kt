package com.devup.android_shopping_mall.data.notification.model

data class Notification (
    val id: Int,
    val unique_id: Int,
    val profile_nickname: String,
    val type_num : Int,
    val type: String,
    val content: String,
    val is_read: Int,
    val post_category_id: Int?
)