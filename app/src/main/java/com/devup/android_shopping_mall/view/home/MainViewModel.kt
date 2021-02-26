package com.devup.android_shopping_mall.view.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devup.android_shopping_mall.data.notification.model.Notification
import com.devup.android_shopping_mall.data.notification.source.repository.NotificationRepository
import com.devup.android_shopping_mall.data.user.model.User
import com.devup.android_shopping_mall.data.user.source.repository.TokenRepository
import com.devup.android_shopping_mall.data.user.source.repository.UserRepository
import com.devup.android_shopping_mall.util.TOKEN_EXPIRED

class MainViewModel(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository

) : ViewModel() {
    private val TAG = "MainViewModel"

    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> = _userProfile

    //로그인 여부
    private val _userProfileExist = MutableLiveData<Boolean>().apply { value = false }
    val userProfileExist: LiveData<Boolean> = _userProfileExist

    var _isExistAuthor = MutableLiveData<Boolean>().apply { value = false }
    val isExistAuthor: LiveData<Boolean> = _isExistAuthor


    private val _isExistData = MutableLiveData<Boolean>().apply { value = false }
    val isExistData: LiveData<Boolean> = _isExistData

    private val _notificationCount = MutableLiveData<Int>()
    val notificationCount: LiveData<Int> = _notificationCount

    private val _unreadCount = MutableLiveData<Int>()
    val unreadCount: LiveData<Int> = _unreadCount

    var _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications

    private val _isNextPage = MutableLiveData<Boolean>()
    val isNextPage: LiveData<Boolean> = _isNextPage

    private val _uniqueId = MutableLiveData<Int>()
    val uniqueId: LiveData<Int> = _uniqueId

    private val _openDetail = MutableLiveData<Boolean>().apply { value = false }
    val openDetail: LiveData<Boolean> = _openDetail


    private var page = 1
    private val limit = 10

    init {
        loadProfile()
        //getAccessToken()
    }

    fun loadProfile() {
        userRepository.getUserProfile(
            onProfileExist = { userProfile ->
                _userProfileExist.value = true
                _userProfile.value = userProfile
            },
            onProfileNotExist = {
                Log.d(TAG, "loadProfile: onProfileNotExist")
                _userProfileExist.value = false

            }
        )

    }

    fun getAccessToken() {
        tokenRepository.getAccessToken(
            accessTokenExisted = { token ->
                _isExistAuthor.value = true
                Log.d(TAG, "getAccessToken,isExistAuthor.value = true: ")
            },
            accessTokenNotExist = {
                _isExistAuthor.value = false
                Log.d(TAG, "getAccessToken,isExistAuthor.value = false: ")
            }
        )
    }

    fun loadNotifications() {
        _notifications.value = null
        notificationRepository.getNotifications(page, limit,
            onSuccess = { response ->
                _notificationCount.value = response.notification_count
                _unreadCount.value = response.unread_count
                if (response.notifications.isNullOrEmpty()) {
                    if (notifications.value.isNullOrEmpty()) {
                        _isExistData.value = false
                        Log.d(TAG, "notifications.value.isNullOrEmpty()")
                    }
                } else {
                    _isExistData.value = true
                    _isNextPage.value = response.is_next
                    _notifications.value = notifications.value?.plus(response.notifications) ?: response.notifications
                    Log.d(TAG, "loadNotifications: ${response.notifications[0].content}")
                }
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    //code=access_token_expired
                    TOKEN_EXPIRED -> {
                        deleteAccessToken()
                        deleteProfile()
                    }
                    else -> Log.d(TAG, "loadNotifications : $notSuccessStatus ")
                }
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loadNotifications $failureError")
            }
        )
    }

    fun modifyNotifications(id: Int) {
        notificationRepository.modifyNotifications(id,
            onSuccess = {
                //목록 지우로 다시 받기.
                /* _notifications.value = null
                 loadNotifications()*/

            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of modifyNotifications $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of modifyNotifications $failureError")
            }
        )
    }

    //리사이클러뷰에 데이터 추가
    fun loadMoreNotifications() {
        _isNextPage.value = false
        page++
        loadNotifications()
    }

    fun clearNotifications() {
        _notifications.value = null
    }

    private fun deleteAccessToken() {
        tokenRepository.deleteToken()
    }

    private fun deleteProfile() {
        userRepository.deleteProfile()
    }

    fun openPostDetail() {
        /*_uniqueId.value = uniqueId
        startDetail()*/

        _openDetail.value = true
    }

    private fun startDetail() {
        _openDetail.value = true
    }


}