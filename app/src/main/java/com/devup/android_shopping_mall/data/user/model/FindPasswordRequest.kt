package com.devup.android_shopping_mall.data.user.model

data class FindPasswordRequest(
    val registered_type: String = "email",
    val profile_email: String
)