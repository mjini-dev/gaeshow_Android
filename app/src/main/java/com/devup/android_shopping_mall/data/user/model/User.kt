package com.devup.android_shopping_mall.data.user.model


/**
 * user정보 데이터클래스
 */
data class User(
    val user_id: Int,
    val profile_nickname: String,
    val profile_email: String?,
    val profile_image_url: String?,
    val created_at: String,

    val registered_type: String,
    val profile_birthday: String?,
    val profile_gender: String?,
    val job_type: String?,
    val job_field: String?,
    val experience_years: String?,
    val working_area: String?,
    val working_area_detail: String?,
    val basic_salary: Int?,
    val longevity: Int?,
    val portfolio_url: String?,
    val github_url: String?,
    val is_information_open: Int,
    val push_status: Int,
    val ides: List<UserLanguageIde>,
    val languages: List<UserLanguageIde>
)