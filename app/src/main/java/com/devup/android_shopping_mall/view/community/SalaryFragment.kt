package com.devup.android_shopping_mall.view.community

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.FragmentSalaryBinding
import com.devup.android_shopping_mall.view.community.adapter.ListAdapter
import com.devup.android_shopping_mall.view.community.viewmodel.BoardDetailsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_salary.view.*
import org.koin.android.ext.android.inject

class SalaryFragment : Fragment() {
    private val TAG = "SalaryFragment"

    private val viewModel: BoardDetailsViewModel by inject()
    private val categoryId = 6
    private lateinit var callback: OnBackPressedCallback

    var job_filter: String = "job_type"
    var experience_years_filter: Int? = 0


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

        val binding: FragmentSalaryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_salary, container, false)
        val view = binding.root
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@SalaryFragment
            //viewModel.loadSalaryDetails(job_filter, experience_years_filter)
            viewModel.loadSalaryTop()
            view.rvSalaryTop3List.let {
                it.adapter = ListAdapter(categoryId,
                object : ListAdapter.PostItemClickListener {
                    override fun onPostItemClick(postId: Int) {
                    }

                })
            }
        }


        val main = activity as AppCompatActivity
        val actionBar: ActionBar = main.supportActionBar!!
        main.tvTitleToolbar.text = getText(R.string.navi_community_str)
        //ActionBar 뒤로가기버튼 비활성화
        actionBar.setDisplayHomeAsUpEnabled(false)
//----- 커뮤니티 -네비게이션 드로어 open 버튼
        main.imageBtnDrawer.visibility = View.VISIBLE
        main.imageBtnDrawer.setOnClickListener { main.drawerLayout.openDrawer(GravityCompat.START) }

        main.btnBackToolbar.visibility = View.GONE
        //main.btnBackToolbar.setOnClickListener { navController.navigate(R.id.action_navigationSalary_to_navigationWorkspace) }

        main.imageBtnBell.isEnabled = true
        main.imageBtnBell.setOnClickListener {
            navController.navigate(R.id.action_navigationSalary_to_notificationsFragment)
            Log.d(TAG, "onCreateView, main.imageBtnBell : ")
        }

        //스피너
        view.spRank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if(position == 0) {
                    experience_years_filter = null
                } else {
                    experience_years_filter = position-1
                }
                viewModel.loadSalaryDetails(job_filter, experience_years_filter)
            }
        }

        view.spJobType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                when (position) {
                    0 -> {
                        job_filter = "job_type"
                        viewModel.loadSalaryDetails(job_filter, experience_years_filter)

                    }
                    else -> {
                        job_filter = "job_field"
                        viewModel.loadSalaryDetails(job_filter, experience_years_filter)

                    }
                }
            }
        }




        return view
    }
//-----onCreateView종료

}