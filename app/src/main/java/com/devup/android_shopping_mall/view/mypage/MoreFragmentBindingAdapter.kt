package com.devup.android_shopping_mall.view.mypage

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.community.model.Post
import com.devup.android_shopping_mall.view.community.adapter.ListAdapter

@BindingAdapter("setMyProfileImage")
fun setMyProfileImage(view: de.hdodenhof.circleimageview.CircleImageView, profile_image_url: String?) {
    // GlideApp.with(view.context).load(profile_image_url).into(view)
    if (profile_image_url.isNullOrEmpty()) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.VISIBLE
        Glide.with(view.context).load(profile_image_url).error(R.drawable.default_img_3x).into(view)
    }
}


@BindingAdapter("setWorkSpace")
fun setWorkSpace(view: RecyclerView, item: List<Post>?) {
    (view.adapter as? ListAdapter)?.run {
        item?.let { updateItems(it) }
        notifyDataSetChanged()
    }
}


@BindingAdapter("clicks")
fun listenClicks(spinner: AppCompatSpinner, result: ObservableField<String>) {
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            result.set(parent?.getItemAtPosition(position) as String)
        }
    }
}

@BindingAdapter("setSpinner")
fun setSpinner(spinner: Spinner, position: Int) {
    spinner.setSelection(position)
}


@BindingAdapter("setLanguageIdeItem")
fun setLanguageIdeItem(view: RecyclerView, items: List<String>?) {
    (view.adapter as? MypageEditLanguageIdeAdapter)?.run {
        items?.let { addItem(it) }
        notifyDataSetChanged()
    }
}

@BindingAdapter("setSearch")
fun setSearch(view: RecyclerView, items: List<String>?) {
    (view.adapter as? LanguageIdeAddAdapter)?.run {
        items?.let { addItem(it) }
        notifyDataSetChanged()
    }
}

/*@BindingAdapter(value = ["setSpinnerExperience", "entry"])
fun setSpinnerExperience(spinner: Spinner, experience: String, entry: Array<String>) {

    val index = entry.indexOf(experience)
    spinner.setSelection(index)
}*/
/*@BindingAdapter("setSpinnerExperience")
fun setSpinnerExperience(spinner: Spinner, experience: String, entry: Array<String>) {

    val index = entry.indexOf(experience)
    spinner.setSelection(index)
}*/
