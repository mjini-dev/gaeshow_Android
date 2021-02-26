package com.devup.android_shopping_mall.data.services.source.repository

import com.devup.android_shopping_mall.data.services.model.ServicesPost
import com.devup.android_shopping_mall.data.services.model.ServicesPostsRequest
import com.devup.android_shopping_mall.data.services.model.ServicesPostsResponse

interface ServicesPostsRepository {

    fun getServicesPosts(
        request: ServicesPostsRequest,
        onSuccess: (response: ServicesPostsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun getServicesAuthPosts(
        request: ServicesPostsRequest,
        onSuccess: (response: ServicesPostsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun getServicesPostDetails(
        resourceId:Int,
        onSuccess: (response: ServicesPost) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )
}