package com.devup.android_shopping_mall.data.community.model

import com.devup.android_shopping_mall.data.tag.Tag

data class Post(
    val post_id: Int,
    val title: String,
    val content: String,
    val profile_nickname: String,
    val experience_years: String,
    val working_area: String,
    val job_type: String,
    val profile_image_url: String,
    val date: String,
    val release_date: String,
    val user_id: Int,
    val thumbnail: String,
    val liked: Int,
    val view_count: Int,
    val comment_count: Int,
    val share_count: Int,
    val bookmark_count: Int,
    val tags: List<Tag>,

    val category_id: Int,
    val rating_id: Int,
    val bookmark_id: Int,
    val attachs: List<Attach>
)