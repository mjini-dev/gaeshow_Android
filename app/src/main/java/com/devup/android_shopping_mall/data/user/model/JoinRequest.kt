package com.devup.android_shopping_mall.data.user.model

import kotlinx.serialization.Serializable


/**
 * 회원가입 서버 요청 데이터클래스
 */
@Serializable
data class JoinRequest(
    val registered_type: String,
    val profile_nickname: String,
    val push_status: Int, // ( 푸시미승인 : 0 , 승인 : 1 )
    val is_information_open: Int,

    val unique_id: String?,
    val profile_email: String?,
    val password: String?,
    val profile_image_url: String?,

/*
    val profile_birthday: String?,
    val job_type: String?,
    val job_field: String?,
    val profile_gender: String?,

    val experience_years: Int?,
    val longevity: Int?,
    val working_area: String?,
    val working_area_detail: String?,
    val basic_salary: Int?,
    val portfolio_url: String?,
    val github_url: String?,

    val affiliated: Int = 0,
    val development_languages: List<DevelopmentLanguages>?,
    val development_ides: List<DevelopmentIdes>?,*/
    val device_os: String
)