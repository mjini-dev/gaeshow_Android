package com.devup.android_shopping_mall.data.community.source.repository

import com.devup.android_shopping_mall.data.community.model.*
import com.devup.android_shopping_mall.data.community.source.remote.PostsRemoteDataSource

class PostsRepositoryImpl(private val postsRemoteDataSource: PostsRemoteDataSource) :
    PostsRepository {

    override fun getPosts(
        request: PostsRequest,
        onSuccess: (response: PostsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        postsRemoteDataSource.getPosts(request,onSuccess, notSuccessStatus, onFailure)
    }

    override fun getPostDetails(
        resourceId: Int,
        onSuccess: (response: Post) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit) {
        postsRemoteDataSource.getPostDetails(resourceId,onSuccess, notSuccessStatus, onFailure)
    }

    override fun getPostDetailsAuth(resourceId: Int, onSuccess: (response: Post) -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        postsRemoteDataSource.getPostDetailsAuth(resourceId,onSuccess, notSuccessStatus, onFailure)
    }

    override fun getPostRecommendsDetails(
        type: String,
        unique_id: Int,
        onSuccess: (response: PostsRecommendsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        postsRemoteDataSource.getPostRecommendsDetails(type, unique_id, onSuccess, notSuccessStatus, onFailure)
    }

    override fun getPostSalariesDetails(
        job_filter: String,
        experience_years_filter: Int?,
        onSuccess: (response: PostsSalaryResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        postsRemoteDataSource.getPostSalariesDetails(job_filter, experience_years_filter, onSuccess, notSuccessStatus, onFailure)
    }

    override fun getSalariesTop(onSuccess: (response: PostsSalaryTopResponse) -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        postsRemoteDataSource.getSalariesTop(onSuccess, notSuccessStatus, onFailure)
    }
}