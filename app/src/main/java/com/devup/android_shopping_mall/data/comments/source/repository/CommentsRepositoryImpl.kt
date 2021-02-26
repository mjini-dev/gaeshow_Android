package com.devup.android_shopping_mall.data.comments.source.repository

import com.devup.android_shopping_mall.data.comments.model.*
import com.devup.android_shopping_mall.data.source.remote.CommentsRemoteDataSource

class CommentsRepositoryImpl(
    private val commentsRemoteDataSource: CommentsRemoteDataSource
) : CommentsRepository {

    override fun getComments(
        resourceId: Int,
        //request: CommentsRequest,
        onSuccess: (response: Comments) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        //commentsRemoteDataSource.getComments(resourceId, request, onSuccess, onFailure)
        commentsRemoteDataSource.getComments(resourceId, onSuccess, onFailure)
    }

    override fun getCommentsAuth(resourceId: Int, onSuccess: (response: Comments) -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        commentsRemoteDataSource.getCommentsAuth(resourceId, onSuccess, notSuccessStatus, onFailure)
    }

    override fun getPostRecommendsComments(type: String, unique_id: Int, request: CommentsRequest, onSuccess: (response: Comments) -> Unit, onFailure: (e: Throwable) -> Unit) {
        commentsRemoteDataSource.getPostRecommendsComments(type, unique_id, request, onSuccess, onFailure)
    }

    override fun addComment(
        resourceId: Int,
        request: AddCommentRequest,
        onSuccess: (response: AddCommentResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        commentsRemoteDataSource.addComment(resourceId, request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun modifyComment(
        resourceId: Int,
        comment_id: Int,
        request: AddCommentRequest,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        commentsRemoteDataSource.modifyComment(resourceId, comment_id, request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun deleteComment(resourceId: Int, comment_id: Int, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        commentsRemoteDataSource.deleteComment(resourceId, comment_id, onSuccess, notSuccessStatus, onFailure)
    }

    override fun addPostRecommendsComment(
        request: AddRecommendCommentRequest,
        onSuccess: (response: AddCommentResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        commentsRemoteDataSource.addPostRecommendsComment(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun deletePostRecommendsComment(comment_id: Int, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        commentsRemoteDataSource.deletePostRecommendsComment(comment_id, onSuccess, notSuccessStatus, onFailure)
    }

    override fun modifyPostRecommendsComment(
        comment_id: Int,
        request: AddRecommendCommentRequest,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        commentsRemoteDataSource.modifyPostRecommendsComment(comment_id, request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun getServicesComments(
        resourceId: Int,
        onSuccess: (response: ServiceComments) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        commentsRemoteDataSource.getServicesComments(resourceId, onSuccess, onFailure)

    }

    override fun addServicesComment(
        resourceId: Int,
        request: AddCommentRequest,
        onSuccess: (response: AddCommentResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        commentsRemoteDataSource.addServicesComment(resourceId, request, onSuccess, notSuccessStatus, onFailure)
    }
}