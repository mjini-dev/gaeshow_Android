package com.devup.android_shopping_mall.data.post.source.repository

import com.devup.android_shopping_mall.data.post.model.*
import com.devup.android_shopping_mall.data.post.source.remote.PostRemoteDataSource
import com.devup.android_shopping_mall.data.uploadfile.UploadFileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody


class PostRepositoryImpl(private val postsRemoteDataSource: PostRemoteDataSource) : PostRepository {


    override fun addPost(
        request: PostAddRequest,
        onSuccess: (response: PostAddResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        postsRemoteDataSource.addPost(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun addServicesPost(
        request: ServicesPostAddRequest,
        onSuccess: (response: PostAddResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        postsRemoteDataSource.addServicesPost(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun uploadFile(
        request: RequestBody,
        file: MultipartBody.Part,
        onSuccess: (response: List<UploadFileResponse>) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        postsRemoteDataSource.uploadFile(request, file, onSuccess, notSuccessStatus, onFailure)
    }

    override fun modifyPost(resourceId: Int, request: PostAddRequest, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        postsRemoteDataSource.modifyPost(resourceId, request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun deletePost(resourceId: Int, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        postsRemoteDataSource.deletePost(resourceId, onSuccess, notSuccessStatus, onFailure)
    }

    override fun ratingsAddPost(
        request: PostRatingsAddRequest,
        onSuccess: (response: PostRatingsAddResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        postsRemoteDataSource.ratingsAddPost(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun ratingsDeletePost(resourceId: Int, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        postsRemoteDataSource.ratingsDeletePost(resourceId, onSuccess, notSuccessStatus, onFailure)
    }

    override fun bookmarksAddPost(
        request: PostBookmarksAddRequest,
        onSuccess: (response: PostBookmarksAddResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        postsRemoteDataSource.bookmarksAddPost(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun bookmarksDeletePost(resourceId: Int, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        postsRemoteDataSource.bookmarksDeletePost(resourceId, onSuccess, notSuccessStatus, onFailure)
    }


}