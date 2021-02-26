package com.devup.android_shopping_mall.data.comments.source.repository

import com.devup.android_shopping_mall.data.comments.model.*


interface CommentsRepository {

    fun getComments(
        resourceId:Int,
        //request: CommentsRequest,
        onSuccess: (response: Comments) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun getCommentsAuth(
        resourceId: Int,
        onSuccess: (response: Comments) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun getPostRecommendsComments(
        type: String,
        unique_id: Int,
        request: CommentsRequest,
        onSuccess: (response: Comments) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun addComment(
        resourceId:Int,
        request: AddCommentRequest,
        onSuccess: (response: AddCommentResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun modifyComment(
        resourceId:Int,
        comment_id: Int,
        request: AddCommentRequest,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun deleteComment(
        resourceId:Int,
        comment_id: Int,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun addPostRecommendsComment(
        request: AddRecommendCommentRequest,
        onSuccess: (response: AddCommentResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun deletePostRecommendsComment(
        comment_id: Int,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun modifyPostRecommendsComment(
        comment_id: Int,
        request: AddRecommendCommentRequest,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun getServicesComments(
        resourceId: Int,
        onSuccess: (response: ServiceComments) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun addServicesComment(
        resourceId:Int,
        request: AddCommentRequest,
        onSuccess: (response: AddCommentResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

}