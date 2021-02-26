package com.devup.android_shopping_mall.data.notification.model

data class NotificationsResponse (
    var notification_count: Int,
    var unread_count: Int,
    var is_next: Boolean,
    var notifications: List<Notification>
)