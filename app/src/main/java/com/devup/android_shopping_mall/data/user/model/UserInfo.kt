package com.devup.android_shopping_mall.data.user.model

data class UserInfo(
    val profile_nickname: String,
    val profile_image_url: String?,
    val profile_email: String?,
    val information: String?,
    val is_information_open: Int,
    val following_id: Int,
    val follower: Int,
    val following: Int
)