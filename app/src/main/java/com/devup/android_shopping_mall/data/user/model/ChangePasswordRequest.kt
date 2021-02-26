package com.devup.android_shopping_mall.data.user.model

data class ChangePasswordRequest(
    val registered_type: String = "email",
    val current_password: String,
    val new_password: String
)