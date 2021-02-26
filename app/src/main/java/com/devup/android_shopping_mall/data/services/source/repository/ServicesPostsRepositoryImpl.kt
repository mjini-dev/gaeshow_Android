package com.devup.android_shopping_mall.data.services.source.repository

import com.devup.android_shopping_mall.data.services.model.ServicesPost
import com.devup.android_shopping_mall.data.services.model.ServicesPostsRequest
import com.devup.android_shopping_mall.data.services.model.ServicesPostsResponse
import com.devup.android_shopping_mall.data.services.source.remote.ServicesPostsRemoteDataSource

class ServicesPostsRepositoryImpl(private val servicesPostsRemoteDataSource: ServicesPostsRemoteDataSource) :ServicesPostsRepository{

    override fun getServicesPosts(
        request: ServicesPostsRequest,
        onSuccess: (response: ServicesPostsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        servicesPostsRemoteDataSource.getServicesPosts(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun getServicesAuthPosts(
        request: ServicesPostsRequest,
        onSuccess: (response: ServicesPostsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        servicesPostsRemoteDataSource.getServicesAuthPosts(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun getServicesPostDetails(
        resourceId: Int,
        onSuccess: (response: ServicesPost) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        servicesPostsRemoteDataSource.getServicesPostDetails(resourceId, onSuccess, notSuccessStatus, onFailure)
    }

}