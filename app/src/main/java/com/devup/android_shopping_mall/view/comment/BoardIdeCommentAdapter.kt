package com.devup.android_shopping_mall.view.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.data.comments.model.Comment
import com.devup.android_shopping_mall.databinding.ItemFragIdeRatingListBinding

class BoardIdeCommentAdapter(private val listener: PostItemClickListener) : RecyclerView.Adapter<BoardIdeCommentAdapter.ViewHolder>() {
    val items = mutableListOf<Comment>()

    //아이템 클릭 인터페이스
    interface PostItemClickListener {
        fun onPostItemClick(position: Int)
        //수정
        fun onModifyClick(position: Int)
        //삭제
        fun onDeleteClick(comment_id: Int)
        //유저페이지이동
        fun onUserClick(position: Int)
    }

    //댓글 목록 추가
    fun updateItems(item: List<Comment>) {
        item.let {
            items.clear()
            items.addAll(it)
        }
    }

    //댓글추가
    fun addComment(comment: Comment) {
        items.add(comment)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFragIdeRatingListBinding.inflate(inflater, parent, false)

        return ViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Comment = items[position]
        holder.bind(item)
    }


    class ViewHolder(private val binding: ItemFragIdeRatingListBinding, listener: PostItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.clickListener = listener
        }

        fun bind(item: Comment) {
            with(binding) {
                position = adapterPosition
                comment = item
                executePendingBindings()
            }
        }
    }
}
