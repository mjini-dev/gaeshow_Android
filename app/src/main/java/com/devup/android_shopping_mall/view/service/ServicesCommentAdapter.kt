package com.devup.android_shopping_mall.view.service

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.data.comments.model.Comment
import com.devup.android_shopping_mall.data.comments.model.ServiceComments
import com.devup.android_shopping_mall.databinding.ItemActQuestionViewCommentsListBinding

class ServicesCommentAdapter() : RecyclerView.Adapter<ServicesCommentAdapter.ViewHolder>() {
    val items = mutableListOf<ServiceComments>()

    //댓글 목록 추가
    fun updateItems(item: List<ServiceComments>) {
        item.let {
            items.clear()
            items.addAll(it)
        }
    }

    //댓글추가
    fun addComment(comment: ServiceComments) {
        items.add(comment)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemActQuestionViewCommentsListBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: ServiceComments = items[position]
        holder.bind(item)
    }


    class ViewHolder(private val binding: ItemActQuestionViewCommentsListBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

        }

        fun bind(item: ServiceComments) {
            with(binding) {
                position = adapterPosition
                comment = item
                executePendingBindings()
            }
        }
    }
}
