package com.devup.android_shopping_mall.view.community.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.tag.Tag
import com.devup.android_shopping_mall.databinding.ItemTagBoardBinding

class TagAdapter(private val categoryId: Int) : RecyclerView.Adapter<TagAdapter.ViewHolder>() {

    val items = mutableListOf<Tag>()

    fun updateItems(item: List<Tag>) {
        item.let {
            items.clear()
            items.addAll(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTagBoardBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)

    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
        when (categoryId) {
            /* 1 -> {

             }*/
            2 -> {
                holder.tvTag.setTextColor(Color.parseColor("#EA6672"))
                holder.tvTag.setBackgroundResource(R.drawable.shape_board_tag)
            }
            3 -> {
//                holder.tvTag.setTextColor(R.color.color_badmouse)
                holder.tvTag.setTextColor(Color.parseColor("#E8A500"))
                holder.tvTag.setBackgroundResource(R.drawable.shape_badmouse_tag)
            }

            4 -> {
                holder.tvTag.setTextColor(Color.parseColor("#F0975B"))
                holder.tvTag.setBackgroundResource(R.drawable.shape_tips_tag)
            }
            8 ,1-> {
                when (items[position].type) {
                    "platform" -> {
//                        holder.tvTag.setTextColor(R.color.color_portfolio_platform)
                        holder.tvTag.setTextColor(Color.parseColor("#BD52F2"))
                        holder.tvTag.setBackgroundResource(R.drawable.shape_portfolio_platform)
                    }
                    "language" -> {
//                        holder.tvTag.setTextColor(R.color.color_portfolio_language)
                        holder.tvTag.setTextColor(Color.parseColor("#6979F8"))
                        holder.tvTag.setBackgroundResource(R.drawable.shape_portfolio_language)
                    }
                    "ide" -> {
//                        holder.tvTag.setTextColor(R.color.color_portfolio_ide)
                        holder.tvTag.setTextColor(Color.parseColor("#15C48B"))
                        holder.tvTag.setBackgroundResource(R.drawable.shape_portfolio_ide)
                    }
                }
            }

        }

    }


    class ViewHolder(private val binding: ItemTagBoardBinding) : RecyclerView.ViewHolder(binding.root) {

        var tvTag: TextView = binding.root.findViewById(R.id.tvTag)

        fun bind(item: Tag) {
            with(binding) {
                postTag = item
                executePendingBindings()
            }
        }
    }
}
