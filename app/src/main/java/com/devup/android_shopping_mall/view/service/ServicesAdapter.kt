package com.devup.android_shopping_mall.view.service

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.services.model.ServicesPost
import com.devup.android_shopping_mall.databinding.ItemFragFaqBinding
import com.devup.android_shopping_mall.databinding.ItemFragNoticeBinding
import com.devup.android_shopping_mall.databinding.ItemFragQuestionListBinding
import com.devup.android_shopping_mall.databinding.ItemFragReportListBinding


class ServicesAdapter(private val categoryId: Int, private val listener: PostItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
    뷰홀더 클래스를 각 리사이클러뷰 아이템에 맞게 만들고

    온크리뷰홀더에서 뷰 타입에 맞게 뷰홀더 리턴하고..

    온바인드 뷰홀더 할떄
    뷰홀더에 맞게 바인드 시킨다..

    여기서 뷰 타입은 어떻게 바꿀것인가.
    어댑터 생성자에서 뷰타입을 나눌수있는 상수값을 받도록하고
    각 뷰에서 리사이클러뷰에 아댑터를 지정할때 파라메타로 상수값을 넘겨서 구분한다.
     */


    val items = mutableListOf<ServicesPost>()

    //아이템 클릭 인터페이스
    interface PostItemClickListener {
        fun onPostItemClick(postId: Int)
    }

    fun updateItems(item: List<ServicesPost>) {
        item.let {
            items.clear()
            items.addAll(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (categoryId) {
            //공지사항
            1 -> {
                val binding = DataBindingUtil.inflate<ItemFragNoticeBinding>(inflater, R.layout.item_frag_notice, parent, false)
                NoticeViewHolder(binding, listener)
            }
            2 -> {
                val binding = DataBindingUtil.inflate<ItemFragFaqBinding>(inflater, R.layout.item_frag_faq, parent, false)
                FaqViewHolder(binding, listener)
            }
            3 -> {
                val binding = DataBindingUtil.inflate<ItemFragQuestionListBinding>(inflater, R.layout.item_frag_question_list, parent, false)
                QuestionViewHolder(binding, listener)
            }
            4 -> {
                val binding = DataBindingUtil.inflate<ItemFragReportListBinding>(inflater, R.layout.item_frag_report_list, parent, false)
                ReportViewHolder(binding, listener)
            }
            else -> throw Exception("Unknow viewType $viewType")
        }
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(categoryId) {
            1 -> {
                (holder as NoticeViewHolder).bind(items[position])
                val isExpandable : Boolean = items[position].isClicked
                (holder as NoticeViewHolder).tvNoticeContent.visibility = if (isExpandable) View.VISIBLE else View.GONE

                (holder as NoticeViewHolder).notice.setOnClickListener {
                    val item = items[position]
                    item.isClicked = !item.isClicked
                    notifyItemChanged(position)
                }
            }
            2-> {
                (holder as FaqViewHolder).bind(items[position])
                val isExpandable : Boolean = items[position].isClicked
                (holder as FaqViewHolder).linearQna.visibility = if (isExpandable) View.VISIBLE else View.GONE

                (holder as FaqViewHolder).faq.setOnClickListener {
                    val item = items[position]
                    item.isClicked = !item.isClicked
                    notifyItemChanged(position)
                }

            }
            3-> (holder as QuestionViewHolder).bind(items[position])
            4-> (holder as ReportViewHolder).bind(items[position])
        }
    }


    class NoticeViewHolder(private val binding: ItemFragNoticeBinding, listener: PostItemClickListener) : RecyclerView.ViewHolder(binding.root) {

        var notice : ConstraintLayout = binding.root.findViewById(R.id.notice)
        var tvNoticeContent : TextView = binding.root.findViewById(R.id.tvNoticeContent)

        init {
            binding.clickListener = listener
        }

        fun bind(item: ServicesPost) {
            with(binding) {
                position = adapterPosition
                post = item
                executePendingBindings()
            }
        }
    }

    class FaqViewHolder(private val binding: ItemFragFaqBinding, listener: PostItemClickListener) : RecyclerView.ViewHolder(binding.root) {

        var faq: ConstraintLayout = binding.root.findViewById(R.id.faq)
        var linearQna: LinearLayout = binding.root.findViewById(R.id.linearQna)

        init {
            binding.clickListener = listener
        }

        fun bind(item: ServicesPost) {
            with(binding) {
                position = adapterPosition
                post = item
                executePendingBindings()
            }

        }
    }

    class QuestionViewHolder(private val binding: ItemFragQuestionListBinding, listener: PostItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clickListener = listener
        }
        fun bind(item: ServicesPost) {
            with(binding) {
                position = adapterPosition
                post = item
                executePendingBindings()
            }
        }
    }

    class ReportViewHolder(private val binding: ItemFragReportListBinding, listener: PostItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clickListener = listener
        }
        fun bind(item: ServicesPost) {
            with(binding) {
                position = adapterPosition
                post = item
                executePendingBindings()
            }
        }
    }
}
