package com.devup.android_shopping_mall.view.service

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.data.comments.model.Comment
import com.devup.android_shopping_mall.data.comments.model.ServiceComments
import com.devup.android_shopping_mall.data.services.model.ServicesPost

@BindingAdapter("setServicesPost")
fun setServicesPost(view: RecyclerView, item: List<ServicesPost>?) {
    (view.adapter as? ServicesAdapter)?.run {
        item?.let { updateItems(it) }
        notifyDataSetChanged()
    }
}

@BindingAdapter("setServicesComment")
fun setServicesComment(view: RecyclerView, item: List<ServiceComments>?) {
    (view.adapter as? ServicesCommentAdapter)?.run {
        item?.let { updateItems(it) }
        notifyDataSetChanged()
    }
}


