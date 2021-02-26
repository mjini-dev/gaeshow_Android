package com.devup.android_shopping_mall.data.user.model

import kotlinx.serialization.Serializable


@Serializable
data class ModifyUserInfoRequest(
    //val profile_email: String?,

    val profile_nickname: String,
    val profile_image_url: String?,
    val profile_birthday: String?,

    val job_type_category_id: Int?,
    val job_field_category_id: Int?,

    val profile_gender: String?,
    val experience_years: Int?,

    val working_area: String?,
    val working_area_detail: String?,

    val basic_salary: Int?,
    val longevity: Int?,

    val development_languages: List<UserLanguageIde>?,
    val development_ides: List<UserLanguageIde>?,

    val github_url: String?,
    val portfolio_url: String?,

    val is_information_open: Int,
    val push_status: Int, // ( 푸시미승인 : 0 , 승인 : 1 )

    val device_os: String,
    val affiliated: Int = 0
    )