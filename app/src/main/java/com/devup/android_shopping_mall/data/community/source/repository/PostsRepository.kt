package com.devup.android_shopping_mall.data.community.source.repository

import com.devup.android_shopping_mall.data.community.model.*

interface PostsRepository {

    fun getPosts(
        request: PostsRequest,
        onSuccess: (response: PostsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun getPostDetails(
        resourceId:Int,
        onSuccess: (response: Post) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun getPostDetailsAuth(
        resourceId:Int,
        onSuccess: (response: Post) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun getPostRecommendsDetails(
        type: String,
        unique_id: Int,
        onSuccess: (response: PostsRecommendsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun getPostSalariesDetails(
        job_filter: String,
        experience_years_filter: Int?,
        onSuccess: (response: PostsSalaryResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun getSalariesTop(
        onSuccess: (response: PostsSalaryTopResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )
}