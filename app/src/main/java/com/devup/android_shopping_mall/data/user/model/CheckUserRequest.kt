package com.devup.android_shopping_mall.data.user.model



/**
 * user 가입여부 확인 서버 요청 데이터클래스
 */
data class CheckUserRequest (
    val registered_type: String,
    val unique_id: String?,
    val profile_email: String?
)