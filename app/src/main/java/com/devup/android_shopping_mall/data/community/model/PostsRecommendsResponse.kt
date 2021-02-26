package com.devup.android_shopping_mall.data.community.model

data class PostsRecommendsResponse (
    val developer_name:String?,
    val average_score: Int?,
    var preference_by_developer: List<PreferenceDeveloper>?,
    var preference_by_career: List<PreferenceCareer>?
)