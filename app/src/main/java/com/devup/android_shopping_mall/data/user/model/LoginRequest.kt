package com.devup.android_shopping_mall.data.user.model


/**
 * 로그인 서버 요청 데이터클래스
 */

data class LoginRequest(
    val registered_type: String,
    val device_os: String,
    val signin_token: String,

    val app_version: String?,
    val device_id: String?,
    val device_model: String?,
    val unique_id: String?,
    val profile_email: String?,
    val password: String?
)