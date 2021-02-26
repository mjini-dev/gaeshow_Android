package com.devup.android_shopping_mall.view.service

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.devup.android_shopping_mall.R
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_customer_service.*

class CustomerServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_service)

        val tabLayoutTextArray = arrayOf("공지사항","자주하는질문","문의사항", "신고목록조회")
        //뷰페이저와 프래그먼트를 연결
        viewpagerCustomerService.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(tabLayoutCustomerService,viewpagerCustomerService) { tab, position ->
            tab.text = tabLayoutTextArray[position]

        }.attach()

        btnCloseToolbar.setOnClickListener {
            finish()
        }

    }

    //뷰페이저 어댑터
    private inner class ViewPagerAdapter (FragmentActivity : FragmentActivity) : FragmentStateAdapter(FragmentActivity) {
        val PAGE_CNT = 4
        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> NoticeFragment()
                1 -> FaqFragment()
                2 -> QuestionListFragment()
//                3 -> CsReportFragment()
                else -> ReportListFragment()
            }
        }
        override fun getItemCount(): Int = PAGE_CNT
    }
}