package com.devup.android_shopping_mall.view.community.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.ItemAddTagBoardBinding
import com.devup.android_shopping_mall.view.community.bindingadapter.onPostItemClickListener
import kotlinx.android.synthetic.main.item_add_tag_footer.view.*

class BoardWriteTagAddAdapter(
    private val categoryId: Int,
    private val tagType: String,
    private val listener: OnItemListener,
    private val tagClick: (adapterPosition: Int) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //footer(x태그추가 버튼)설정
    private val TYPE_ITEM = 1
    private val TYPE_FOOTER = 2

    private val tags = mutableListOf<String>()

    fun addTags(items: List<String>) {
        items.let {
            tags.clear()
            tags.addAll(it)
        }
    }

    interface OnItemListener {
        fun footerClick()
    }

    private lateinit var mListener: OnItemListener

    fun setOnClickListener(listener: OnItemListener) {
        this.mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        TYPE_FOOTER -> {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_tag_footer, parent, false)
            FooterViewHolder(view, listener)
        }
        TYPE_ITEM -> {
            val inflater = LayoutInflater.from(parent.context)
//            val binding = ItemAddTagBoardBinding.inflate(inflater, parent, false)
//            ViewHolder(binding, listener)
            //val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_tag_board, parent, false)

            val binding = DataBindingUtil.inflate<ItemAddTagBoardBinding>(
                inflater,
                R.layout.item_add_tag_board,
                parent,
                false
            )
            ViewHolder(binding, tagClick)

        }
        else -> throw Exception("Unknow viewType $viewType")
    }

    override fun getItemCount(): Int {
        return tags.size + 1
    }

    override fun getItemViewType(position: Int): Int =
        when (position) {
            tags.size -> TYPE_FOOTER
            else -> TYPE_ITEM
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_ITEM -> {
                (holder as ViewHolder).bind(tags[position])
                when (categoryId) {
                    2 -> {
                        holder.tvTag.setTextColor(Color.parseColor("#EA6672"))
                        holder.addTag.setBackgroundResource(R.drawable.shape_board_tag)
                    }
                    3 -> {
                        holder.tvTag.setTextColor(Color.parseColor("#E8A500"))
                        holder.addTag.setBackgroundResource(R.drawable.shape_badmouse_tag)
                    }
                    4 -> {
                        holder.tvTag.setTextColor(Color.parseColor("#F0975B"))
                        holder.addTag.setBackgroundResource(R.drawable.shape_tips_tag)
                    }

                    8 -> {
                        when (tagType) {
                            "platform" -> {
                                holder.tvTag.setTextColor(Color.parseColor("#BD52F2"))
                                holder.addTag.setBackgroundResource(R.drawable.shape_portfolio_platform)
                            }
                            "language" -> {
                                holder.tvTag.setTextColor(Color.parseColor("#6979F8"))
                                holder.addTag.setBackgroundResource(R.drawable.shape_portfolio_language)
                            }
                            "ide" -> {
                                holder.tvTag.setTextColor(Color.parseColor("#15C48B"))
                                holder.addTag.setBackgroundResource(R.drawable.shape_portfolio_ide)
                            }

                        }
                    }

                }
            }
        }
    }

    class FooterViewHolder(itemView: View, listener: OnItemListener) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.btnTagging.setOnClickListener {
                listener.footerClick()
            }
        }
    }

    class ViewHolder(private val binding: ItemAddTagBoardBinding, tagClick: (adapterPosition: Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        var addTag: ConstraintLayout = binding.root.findViewById(R.id.addTag)
        var tvTag: TextView = binding.root.findViewById(R.id.tvTag)

        init {
            itemView.onPostItemClickListener(
                View.OnClickListener { tagClick(adapterPosition) }
            )
        }

        fun bind(tag: String) {
            with(binding) {
                addedTag = tag
                executePendingBindings()
            }
        }
    }


}
