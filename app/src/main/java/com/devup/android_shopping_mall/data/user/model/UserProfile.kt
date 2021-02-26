package com.devup.android_shopping_mall.data.user.model

data class UserProfile(
    val user_id: Int,
    val profile_image_url: String?,
    val profile_nickname: String,
    val profile_email: String?,
    val job_type: String?,
    val job_field: String?,
    val profile_gender: String?,
    val experience_years: String?,
    val working_area: String?
)