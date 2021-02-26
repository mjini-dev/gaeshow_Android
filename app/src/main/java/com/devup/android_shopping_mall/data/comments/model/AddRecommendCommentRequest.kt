package com.devup.android_shopping_mall.data.comments.model

data class AddRecommendCommentRequest(
    val type: String?,
    val unique_id: Int,
    val average_score: Float,
    val advantage_content: String?,
    val disadvantage_content: String?
)
