package com.devup.android_shopping_mall.data.user.model



/**
 * 회원가입 서버 응답 데이터클래스
 */
data class JoinResponse(
    val user_id: Int,
    val signin_token: String
)