package com.devup.android_shopping_mall.data.user.source.remote

import com.devup.android_shopping_mall.data.user.model.*

/**
 * user조회 API Remote DataSource
 */
interface UserRemoteDataSource {

    fun getTerms(
        onSuccess: (Terms) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun checkUser(
        request: CheckUserRequest,
        onSuccess: (response: CheckUserResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun checkNickname(
        request: CheckNicknameRequest,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun join(
        request: JoinRequest,
        onSuccess: (response: JoinResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun login(
        request: LoginRequest,
        onSuccess: (response: LoginResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun logout(
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun getUserInfo(
        onSuccess: (response: User) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun modifyUserInfo(
        request: ModifyUserInfoRequest,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun changePassword(
        request: ChangePasswordRequest,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun findPassword(
        request: FindPasswordRequest,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )


    fun getOtherUserInfo(
        userId: Int,
        onSuccess: (response: UserInfo) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun getOtherUserInfoAuth(
        userId: Int,
        onSuccess: (response: UserInfo) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun addFollows(
        userId: Int,
        onSuccess: (response: FollowAddResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun deleteFollows(
        resource_id: Int,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

}