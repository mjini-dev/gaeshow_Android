package com.devup.android_shopping_mall.view.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.ItemSignup2IdeBinding
import kotlinx.android.synthetic.main.item_add_ide_footer.view.*

class MypageEditLanguageIdeAdapter(
    private val listener: OnItemListener
   // private val itemClick: (adapterPosition: Int) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //footer(추가 버튼)설정
    private val TYPE_ITEM = 1
    private val TYPE_FOOTER = 2

    private val ides = mutableListOf<String>()

    fun addItem(items: List<String>) {
        items.let {
            ides.clear()
            ides.addAll(it)
        }
    }

    interface OnItemListener {
        fun footerClick()
        fun onItemClick(str:String)
    }

  /*  private lateinit var mListener: OnItemListener

    fun setOnClickListener(listener: OnItemListener) {
        this.mListener = listener
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        TYPE_FOOTER -> {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_ide_footer, parent, false)
            FooterViewHolder(view, listener)
        }
        TYPE_ITEM -> {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ItemSignup2IdeBinding>(
                inflater,
                R.layout.item_signup2_ide,
                parent,
                false
            )
            ViewHolder(binding, listener)

        }
        else -> throw Exception("Unknow viewType $viewType")
    }

    override fun getItemCount(): Int {
        return ides.size + 1
    }

    override fun getItemViewType(position: Int): Int =
        when (position) {
            ides.size -> TYPE_FOOTER
            else -> TYPE_ITEM
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_ITEM -> {
                (holder as ViewHolder).bind(ides[position])
            }
        }
    }

    class FooterViewHolder(itemView: View, listener: OnItemListener) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.LanguagesAdd.setOnClickListener {
                listener.footerClick()
            }
        }
    }

    //class ViewHolder(private val binding: ItemSignup2IdeBinding, itemClick: (adapterPosition: Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {
    class ViewHolder(private val binding: ItemSignup2IdeBinding, listener: OnItemListener) : RecyclerView.ViewHolder(binding.root) {

        /*init {
            itemView.onPostItemClickListener(
                View.OnClickListener { itemClick(adapterPosition) }
            )
        }*/




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
