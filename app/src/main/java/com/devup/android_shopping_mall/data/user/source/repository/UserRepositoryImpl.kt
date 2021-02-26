package com.devup.android_shopping_mall.data.user.source.repository

import com.devup.android_shopping_mall.data.user.model.*
import com.devup.android_shopping_mall.data.user.source.local.UserLocalDataSource
import com.devup.android_shopping_mall.data.user.source.remote.UserRemoteDataSource

class UserRepositoryImpl(
    private val userLocalDataSource: UserLocalDataSource,
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {


    override fun getTerms(onSuccess: (Terms) -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        userRemoteDataSource.getTerms(onSuccess, notSuccessStatus, onFailure)
    }

    override fun checkUser(
        request: CheckUserRequest,
        onSuccess: (response: CheckUserResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        userRemoteDataSource.checkUser(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun checkNickname(request: CheckNicknameRequest, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        userRemoteDataSource.checkNickname(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun join(
        request: JoinRequest,
        onSuccess: (response: JoinResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        userRemoteDataSource.join(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun login(
        request: LoginRequest,
        onSuccess: (response: LoginResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        userRemoteDataSource.login(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun logout(
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        userRemoteDataSource.logout(onSuccess, notSuccessStatus, onFailure)
    }

    override fun getUserInfo(onSuccess: (response: User) -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        userRemoteDataSource.getUserInfo(onSuccess, notSuccessStatus, onFailure)
    }

    override fun modifyUserInfo(request: ModifyUserInfoRequest, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        userRemoteDataSource.modifyUserInfo(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun changePassword(request: ChangePasswordRequest, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        userRemoteDataSource.changePassword(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun findPassword(request: FindPasswordRequest, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        userRemoteDataSource.findPassword(request, onSuccess, notSuccessStatus, onFailure)
    }

    override fun getOtherUserInfo(userId: Int, onSuccess: (response: UserInfo) -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        userRemoteDataSource.getOtherUserInfo(userId, onSuccess, notSuccessStatus, onFailure)
    }

    override fun getOtherUserInfoAuth(userId: Int, onSuccess: (response: UserInfo) -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        userRemoteDataSource.getOtherUserInfoAuth(userId, onSuccess, notSuccessStatus, onFailure)
    }

    override fun addFollows(userId: Int, onSuccess: (response: FollowAddResponse) -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        userRemoteDataSource.addFollows(userId, onSuccess, notSuccessStatus, onFailure)
    }

    override fun deleteFollows(resource_id: Int, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        userRemoteDataSource.deleteFollows(resource_id, onSuccess, notSuccessStatus, onFailure)
    }

    override fun saveUserProfile(user: User) {
        userLocalDataSource.saveUserProfile(user)
    }

    override fun deleteProfile() {
        userLocalDataSource.deleteProfile()
    }

    override fun getUserProfile(onProfileExist: (user: User) -> Unit, onProfileNotExist: () -> Unit) {
        userLocalDataSource.getUserProfile(onProfileExist, onProfileNotExist)
    }

    /* override fun getUserProfile(onProfileExist: (profile: UserProfile) -> Unit, onProfileNotExist: () -> Unit) {
         userLocalDataSource.getUserProfile(onProfileExist, onProfileNotExist)
     }*/

    override fun saveSimpleLoginUserProfile(SimpleLoginUserProfile: SimpleLoginUserProfile) {
        userLocalDataSource.saveSimpleLoginUserProfile(SimpleLoginUserProfile)
    }

    override fun getSimpleLoginUserProfile(onProfileExist: (SimpleLoginUserProfile: SimpleLoginUserProfile) -> Unit, onProfileNotExist: () -> Unit) {
        userLocalDataSource.getSimpleLoginUserProfile(onProfileExist, onProfileNotExist)
    }


}