package com.devup.android_shopping_mall.data.community.model

import com.devup.android_shopping_mall.data.community.model.Post

data class PostsResponse (
    var post_count: Int,
    var is_next: Boolean,
    var posts: List<Post>
)