package com.devup.android_shopping_mall.data.user.model


/**
 * user 가입여부 확인 서버 응답 데이터클래스
 */
data class CheckUserResponse(
    val message: String,
    val signin_token: String
)