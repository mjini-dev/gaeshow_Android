package com.devup.android_shopping_mall.view.mypage

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.ItemSignup2LanguageSearchBinding

class LanguageIdeAddAdapter(private val type: String, private val listener: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val items = mutableListOf<String>()

    //아이템 클릭 인터페이스
    interface ItemClickListener {
        fun onItemClick(str:String)
    }

    fun addItem(item: List<String>) {
        item.let {
            items.clear()
            items.addAll(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSignup2LanguageSearchBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, listener)

        /*return when(type) {
            "searchResult"-> {
                val binding = ItemSignup2LanguageSearchBinding.inflate(inflater, parent, false)
                ViewHolder(binding, listener)

            }
            else -> {
                val binding = ItemSignup2LanguageSearchBinding.inflate(inflater, parent, false)
                ViewHolder(binding, listener)
            }

        }*/

    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position])
        when (type) {
            "searchResult"-> { //검은글씨,흰바탕
                holder.tvDevelopmentLanguages.setTextColor(Color.BLACK)
                holder.tvDevelopmentLanguages.setBackgroundResource(R.drawable.shape_textview)
                holder.tvDevelopmentLanguages.setPadding(20)
            }
            "choice" -> {
                holder.tvDevelopmentLanguages.setTextColor(Color.WHITE)
                holder.tvDevelopmentLanguages.setBackgroundResource(R.color.colorBlack)
            }

        }

    }



    class ViewHolder(private val binding: ItemSignup2LanguageSearchBinding, listener: ItemClickListener) : RecyclerView.ViewHolder(binding.root) {

        var tvDevelopmentLanguages:TextView = binding.root.findViewById(R.id.tvDevelopmentLanguages)

        init {
            binding.clickListener = listener
        }

        fun bind(ide: String) {
            with(binding) {
                added = ide
                executePendingBindings()
            }
        }
    }
}
