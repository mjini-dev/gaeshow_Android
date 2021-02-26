package com.devup.android_shopping_mall.view.home

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.data.comments.model.Comment
import com.devup.android_shopping_mall.data.notification.model.Notification
import com.devup.android_shopping_mall.data.services.model.ServicesPost

@BindingAdapter("setNotificationList")
fun setNotificationList(view: RecyclerView, item: List<Notification>?) {
    (view.adapter as? NotificationAdapter)?.run {
        item?.let { updateItems(it) }
        notifyDataSetChanged()
    }
}



