package com.devup.android_shopping_mall.data.services.model

data class ServicesPostsResponse (
    var post_count: Int,
    var is_next: Boolean,
    var posts: List<ServicesPost>
)
