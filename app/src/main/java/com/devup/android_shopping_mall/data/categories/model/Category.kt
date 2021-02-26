package com.devup.android_shopping_mall.data.categories.model


/**
 * Category 데이터클래스
 */
data class Category(
    val id: Int,
    val name: String,
    val explanation: String,
    val writable_num: Int,

    val en_name: String,
    val ko_name: String,
    val job_field: List<JobFieldCategory>
)