package com.devup.android_shopping_mall.view.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.categories.model.Category
import com.devup.android_shopping_mall.data.community.model.Laguages
import com.devup.android_shopping_mall.data.community.model.Post
import com.devup.android_shopping_mall.data.community.model.Salaries
import com.devup.android_shopping_mall.databinding.*

class ListAdapter(private val categoryId: Int, private val listener: PostItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val items = mutableListOf<Post>()
    val categories = mutableListOf<Category>()
    val salaries = mutableListOf<Salaries>()
    val language = mutableListOf<Laguages>()

    //아이템 클릭 인터페이스
    interface PostItemClickListener {
        fun onPostItemClick(postId: Int)
    }

    fun updateItems(item: List<Post>) {
        item.let {
            items.clear()
            items.addAll(it)
        }
    }
    fun updateCategorys(item: List<Category>) {
        item.let {
            categories.clear()
            categories.addAll(it)
        }
    }
    fun updateSalaryTop(item: List<Salaries>) {
        item.let {
            salaries.clear()
            salaries.addAll(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (categoryId) {
            5 -> {
                val binding = ItemFragWorkspaceListBinding.inflate(inflater, parent, false)
                WorkSpaceViewHolder(binding, listener)
            }
            55 -> {
                val binding = ItemThumbnailBinding.inflate(inflater, parent, false)
                ThubnailViewHolder(binding, listener)
            }
            6 -> {
                val binding = ItemFragSalaryTop3ListBinding.inflate(inflater, parent, false)
                SalaryViewHolder(binding, listener)
            }
            7 -> {
                val binding = ItemFragIdeListBinding.inflate(inflater, parent, false)
                IdeViewHolder(binding, listener)
            }
            else -> {
                val binding = ItemFragBoardListBinding.inflate(inflater, parent, false)
                ViewHolder(binding, listener)
            }

        }
    }

    //override fun getItemCount(): Int = items.size
    override fun getItemCount(): Int {
        return when (categoryId) {
            6 -> salaries.size
            7 -> categories.size
            else -> items.size
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (categoryId) {
            5 -> {
                (holder as WorkSpaceViewHolder).bind(items[position])
            }
            55 -> {
                (holder as ThubnailViewHolder).bind(items[position])
            }
            6 -> {
                //(holder as SalaryViewHolder).bind(salaries[position],language[position])
                (holder as SalaryViewHolder).bind(salaries[position])
            }
            7 -> {
                (holder as IdeViewHolder).bind(categories[position])
            }
            else -> {
                (holder as ViewHolder).bind(items[position])
                val tag = items[position].tags
                val tagAdapter = TagAdapter(categoryId)
                tagAdapter.updateItems(tag)
                (holder as ViewHolder).rvTag.adapter = tagAdapter
            }
        }

    }


    class ViewHolder(private val binding: ItemFragBoardListBinding, listener: PostItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        var rvTag: RecyclerView = binding.root.findViewById(R.id.rvTag)

        init {
            binding.clickListener = listener
        }

        fun bind(item: Post) {
            with(binding) {
                position = adapterPosition
                post = item
                executePendingBindings()
            }
        }
    }

    class WorkSpaceViewHolder(private val binding: ItemFragWorkspaceListBinding, listener: PostItemClickListener) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.clickListener = listener
        }

        fun bind(item: Post) {
            with(binding) {
                position = adapterPosition
                post = item
                executePendingBindings()
            }
        }
    }

    class ThubnailViewHolder(private val binding: ItemThumbnailBinding, listener: PostItemClickListener) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.clickListener = listener
        }

        fun bind(item: Post) {
            with(binding) {
                position = adapterPosition
                post = item
                executePendingBindings()
            }
        }
    }

    class IdeViewHolder(private val binding: ItemFragIdeListBinding, listener: PostItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clickListener = listener
        }

        fun bind(item: Category) {
            with(binding) {
                position = adapterPosition
                category = item
                executePendingBindings()
            }
        }
    }

    class SalaryViewHolder(private val binding: ItemFragSalaryTop3ListBinding, listener: PostItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        val tvMainLanguageTag : TextView = binding.root.findViewById(R.id.tvMainLanguageTag)

        fun bind(item: Salaries) {
            with(binding) {
                position = adapterPosition
                salaries = item
                executePendingBindings()
            }
        }
    }

}
