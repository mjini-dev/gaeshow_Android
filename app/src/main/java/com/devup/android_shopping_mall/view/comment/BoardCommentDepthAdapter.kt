package com.devup.android_shopping_mall.view.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.data.comments.model.CommentDepth
import com.devup.android_shopping_mall.databinding.ItemCommentsDepthBinding
import com.devup.android_shopping_mall.view.community.viewmodel.BoardDetailsViewModel

class BoardCommentDepthAdapter(private val viewModel: BoardDetailsViewModel, private val listener: ItemClickListener) :
    RecyclerView.Adapter<BoardCommentDepthAdapter.ViewHolder>() {

    val items = mutableListOf<CommentDepth>()

    //아이템 클릭 인터페이스
    interface ItemClickListener {
        fun onModifyClick(commentId: Int, content: String)
        fun onDeleteClick(commentId: Int)
        fun onRatingClick(ratingId: Int,commentId: Int)
        fun onReportClick(comment_id: Int)
    }

    fun updateItems(item: List<CommentDepth>) {
        item.let {
            items.clear()
            items.addAll(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentsDepthBinding.inflate(inflater, parent, false)

        return ViewHolder(binding, listener, viewModel)
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: CommentDepth = items[position]
        holder.bind(item)
    }


    class ViewHolder(private val binding: ItemCommentsDepthBinding, listener: ItemClickListener, viewModel: BoardDetailsViewModel) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clickListener = listener
            binding.vm = viewModel
        }

        fun bind(item: CommentDepth) {
            with(binding) {
                position = adapterPosition
                comment = item
                executePendingBindings()
            }
        }
    }
}
