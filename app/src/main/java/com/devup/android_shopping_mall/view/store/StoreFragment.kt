package com.devup.android_shopping_mall.view.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.devup.android_shopping_mall.R
import kotlinx.android.synthetic.main.activity_main.*

class StoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_store, null)

        val main = activity as AppCompatActivity
        main.imageBtnBell.isEnabled = false
        val actionBar : ActionBar = (activity as AppCompatActivity).supportActionBar!!
        //actionBar.title = "ddddddddddd"
        (activity as AppCompatActivity).tvTitleToolbar.text = getText(R.string.navi_store_str)
        actionBar.setDisplayHomeAsUpEnabled(false)
//----- 커뮤니티 -네비게이션 드로어 버튼 숨기기
        (activity as AppCompatActivity).imageBtnDrawer.visibility = View.GONE
        (activity as AppCompatActivity).btnCloseToolbar.visibility = View.GONE


        return view
    }
//-----onCreateView종료

}