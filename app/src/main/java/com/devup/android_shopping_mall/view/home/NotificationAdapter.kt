package com.devup.android_shopping_mall.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.data.comments.model.Comment
import com.devup.android_shopping_mall.data.notification.model.Notification
import com.devup.android_shopping_mall.databinding.ItemFragNotificationListBinding


class NotificationAdapter(private val listener: PostItemClickListener) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    val items = mutableListOf<Notification>()

    //아이템 클릭 인터페이스
    interface PostItemClickListener {
        fun onPostItemClick(position: Int)
    }

    fun updateItems(item: List<Notification>) {
        item.let {
            items.clear()
            items.addAll(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFragNotificationListBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Notification = items[position]
        holder.bind(item)
    }

    class ViewHolder(private val binding: ItemFragNotificationListBinding, listener: PostItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clickListener = listener
        }

        fun bind(item: Notification) {
            with(binding) {
                position = adapterPosition
                noti = item
                executePendingBindings()
            }
        }
    }
}
