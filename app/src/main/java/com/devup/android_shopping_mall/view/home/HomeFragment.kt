package com.devup.android_shopping_mall.view.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.devup.android_shopping_mall.R
import kotlinx.android.synthetic.main.activity_main.*

class HomeFragment : Fragment() {
    private val TAG = "HomeFragment"

    lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        val main = activity as AppCompatActivity

        val actionBar : ActionBar = (activity as AppCompatActivity).supportActionBar!!
        //actionBar.title = "ddddddddddd"
        (activity as AppCompatActivity).tvTitleToolbar.text = getText(R.string.navi_home_str)
        actionBar.setDisplayHomeAsUpEnabled(false)
//----- 커뮤니티 -네비게이션 드로어 버튼 숨기기
        (activity as AppCompatActivity).imageBtnDrawer.visibility = View.GONE
        (activity as AppCompatActivity).btnBackToolbar.visibility = View.GONE
        (activity as AppCompatActivity).btnCloseToolbar.visibility = View.GONE

        main.imageBtnBell.isEnabled = true
        main.imageBtnBell.setOnClickListener {
            navController.navigate(R.id.action_navigationHome_to_notificationsFragment)
            Log.d(TAG, "onCreateView, main.imageBtnBell : ")
        }

        return view
    }
//-----onCreateView종료

}