package com.devup.android_shopping_mall.data.comments.model

import java.io.Serializable

data class CommentDepth(
    val comment_id: Int,
    val user_id: Int,
    val profile_nickname: String,
    val profile_image_url: String,
    val job_type: String,
    val job_field: String,
    val experience_years: String,
    val content: String,
    val date: String,
    val liked: Int,
    val is_post_author: Boolean,
    var isCommentAuthor : Boolean
    ,
    val rating_id: Int
) : Serializable