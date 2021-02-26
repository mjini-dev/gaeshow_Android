package com.devup.android_shopping_mall.data.user.model

data class SimpleLoginUserProfile(
    val unique_id: String,
    val profile_nickname: String,
    val profile_image_url: String,
    val profile_email: String?,
    val profile_gender: String?
)