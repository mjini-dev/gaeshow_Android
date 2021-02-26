package com.devup.android_shopping_mall.data.services.source.remote

import com.devup.android_shopping_mall.data.services.model.ServicesPost
import com.devup.android_shopping_mall.data.services.model.ServicesPostsRequest
import com.devup.android_shopping_mall.data.services.model.ServicesPostsResponse

interface ServicesPostsRemoteDataSource {

    //공지사항, 자주하는 질문 목록 조회
    fun getServicesPosts(
        request: ServicesPostsRequest,
        onSuccess: (response: ServicesPostsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    //문의, 신고 목록조회
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