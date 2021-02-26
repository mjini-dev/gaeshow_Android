package com.devup.android_shopping_mall.data.notification.source.repository

import com.devup.android_shopping_mall.data.notification.model.NotificationsResponse
import com.devup.android_shopping_mall.data.notification.source.remote.NotificationRemoteDataSource


class NotofocationRepositoryImpl(private val notificationRemoteDataSource: NotificationRemoteDataSource) :
    NotificationRepository {


    override fun getNotifications(
        page: Int,
        limit: Int,
        onSuccess: (response: NotificationsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        notificationRemoteDataSource.getNotifications(page, limit, onSuccess, notSuccessStatus, onFailure)
    }

    override fun modifyNotifications(resource_id: Int, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        notificationRemoteDataSource.modifyNotifications(resource_id, onSuccess, notSuccessStatus, onFailure)
    }

}