package com.devup.android_shopping_mall.view.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.comments.model.Comment
import com.devup.android_shopping_mall.databinding.ItemFragBoardViewCommentsListBinding
import com.devup.android_shopping_mall.view.community.viewmodel.BoardDetailsViewModel

class BoardCommentAdapter(private val viewModel: BoardDetailsViewModel, private val listener: PostItemClickListener) : RecyclerView.Adapter<BoardCommentAdapter.ViewHolder>() {
    val items = mutableListOf<Comment>()

    //아이템 클릭 인터페이스
    interface PostItemClickListener {
        //댓글달기
        fun onPostItemClick(position: Int)
        //수정
        fun onModifyClick(position: Int)
        //삭제
        fun onDeleteClick(comment_id: Int)
        //평가(좋아요)
        fun onRatingClick(position: Int)
        //신고
        fun onReportClick(comment_id: Int)
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
        val binding = ItemFragBoardViewCommentsListBinding.inflate(inflater, parent, false)

        return ViewHolder(binding, listener, viewModel)
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Comment = items[position]
        holder.bind(item)
    }


    class ViewHolder(private val binding: ItemFragBoardViewCommentsListBinding, listener: PostItemClickListener, viewModel: BoardDetailsViewModel) :
        RecyclerView.ViewHolder(binding.root) {

        var btnModifyComment: Button = binding.root.findViewById(R.id.btnModifyComment)

        init {
            binding.clickListener = listener
            /*binding.btnModifyComment.setOnClickListener {
                Log.d("댓글 바인딩", "수정버튼 클릭했다: ")
            }*/
            binding.vm = viewModel
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
