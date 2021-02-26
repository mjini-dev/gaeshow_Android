package com.devup.android_shopping_mall.data.notification.source.remote

import com.devup.android_shopping_mall.data.notification.model.NotificationsResponse

interface NotificationRemoteDataSource {

    fun getNotifications(
        page: Int,
        limit: Int,
        onSuccess: (response: NotificationsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun modifyNotifications(
        resource_id: Int,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

}