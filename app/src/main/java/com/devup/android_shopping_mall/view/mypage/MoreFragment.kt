package com.devup.android_shopping_mall.view.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.FragmentMoreBinding
import com.devup.android_shopping_mall.view.home.MainViewModel
import com.devup.android_shopping_mall.view.login.LoginActivity
import com.devup.android_shopping_mall.view.service.CustomerServiceActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_more.*
import kotlinx.android.synthetic.main.fragment_more.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MoreFragment : Fragment() {
    private val TAG = "MoreFragment"

    private val viewModel: MoreFragmentViewModel by inject()
    private val mainViewModel: MainViewModel by sharedViewModel()

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
        // val view = inflater.inflate(R.layout.fragment_more, null)
        val binding: FragmentMoreBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false)
        val view = binding.root

        binding.apply {
            vm = viewModel
            lifecycleOwner = this@MoreFragment
        }

        val main = activity as AppCompatActivity
        val actionBar: ActionBar = main.supportActionBar!!

        //actionBar.title = "ddddddddddd"
        main.tvTitleToolbar.text = getText(R.string.navi_more_str)
        actionBar.setDisplayHomeAsUpEnabled(false)
//----- 커뮤니티 -네비게이션 드로어 버튼 숨기기
        main.imageBtnDrawer.visibility = View.GONE
        main.btnBackToolbar.visibility = View.GONE
        main.btnCloseToolbar.visibility = View.GONE

        main.imageBtnBell.isEnabled = true
        main.imageBtnBell.setOnClickListener {
            navController.navigate(R.id.action_navigationMore_to_notificationsFragment)
            Log.d(TAG, "onCreateView, main.imageBtnBell : ")
        }

//-----로그인페이지 이동
        view.btnSignin.setOnClickListener {
            val intent = Intent(view.context, LoginActivity::class.java)
            startActivity(intent)
        }

//-----회원정보수정 페이지 이동
        view.btnModifyUserInfo.setOnClickListener {
            val intent = Intent(view.context, MypageEditActivity::class.java)
            startActivity(intent)
        }

//-----마이페이지 보러가기 페이지 이동
        view.btnMypage.setOnClickListener {
            /*val intent = Intent(view.context, UserPageActivity::class.java)
            val bundle = bundleOf("user_id" to (viewModel.userProfile.value?.user_id ?: 0))
            intent.putExtras(bundle)
            startActivity(intent)*/

            val userId = (viewModel.userProfile.value?.user_id ?: 0)
            val profileNickname = viewModel.userProfile.value?.profile_nickname

            val bundle = bundleOf("user_id" to userId, "profile_nickname" to profileNickname)
            navController.navigate(R.id.action_navigationMore_to_userPageFragment, bundle)

        }

//-----북마크 페이지 이동
        view.btnBookmark.setOnClickListener {
            val userId = (viewModel.userProfile.value?.user_id ?: 0)
            val profileNickname = viewModel.userProfile.value?.profile_nickname

            val bundle = bundleOf("user_id" to userId, "profile_nickname" to profileNickname)
            navController.navigate(R.id.action_navigationMore_to_bookmarkFragment, bundle)
        }

        //----비밀번호 변경
        view.btnPassword.setOnClickListener {
            val intent = Intent(view.context, ChangePasswordActivity::class.java)
            startActivity(intent)
        }


//-----고객센터 페이지 이동
        view.btnCustomerService.setOnClickListener {
            val intent = Intent(view.context, CustomerServiceActivity::class.java)
            startActivity(intent)
        }
//-----개인정보 처리방침 페이지 이동
        view.btnTermsPrivacy.setOnClickListener {
            viewModel.terms.value?.privacy?.let { it1 -> popWebView(it1) }
        }

//-----서비스이용약관 페이지 이동
        view.btnTermsService.setOnClickListener {
            viewModel.terms.value?.service?.let { it1 -> popWebView(it1) }
        }


//        return inflater.inflate(R.layout.fragment_more, container, false)
        //setUserProfile()
        Log.d(TAG, "onCreateView: ")

        //로그아웃
        view.btnLogout.setOnClickListener {
            viewModel.logout()
            setUserProfile()
            // fragmentManager?.let { refreshFragment(this, it) }
        }

        //카톡으로 신고하기
        view.btnReportAsKakao.setOnClickListener {
            alert(R.string.before_service_str)
        }

        return view
    }
    //-----onCreateView종료


    fun alert(strId: Int) {
        var dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(strId)
        dialog.setPositiveButton(R.string.ok_str, null)
        dialog.show()
    }

    private fun popWebView(url: String) {
        val webView = WebView(requireContext()).apply {
            loadUrl(url)
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url != null) {
                        view!!.loadUrl(url)
                    }
                    return true
                }
            }
        }
        AlertDialog.Builder(requireContext())
            .setView(webView)
            .setNegativeButton("닫기") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume ")
        viewModel.loadProfile()
        setUserProfile()
    }

    private fun setUserProfile() {
        Log.d(TAG, "setUserProfile")
        val main = activity as AppCompatActivity

        viewModel.userProfileExist.observe(viewLifecycleOwner, Observer { exist ->
            if (exist) {
                Log.d(TAG, "setUserProfile: exist")
                unSignin.visibility = View.GONE
                onSignin.visibility = View.VISIBLE

                //mainViewModel._isExistAuthor.value = true
                //mainViewModel.getAccessToken()
                main.imageBtnBell.visibility = View.VISIBLE
                mainViewModel.getAccessToken()
                mainViewModel.loadProfile()
                mainViewModel._notifications.value = null
                mainViewModel.loadNotifications()
                Log.d(TAG, "setUserProfile, mainViewModel.unreadCount.value: ${mainViewModel.unreadCount.value}")
                if (mainViewModel.unreadCount.value.toString() == "0") {
                    main.imageBtnBell.setBackgroundResource(R.drawable.ic_bell_thin_3)
                } else  if (mainViewModel.unreadCount.value.toString() == "null") {
                    main.imageBtnBell.setBackgroundResource(R.drawable.ic_bell_thin_3)
                } else main.imageBtnBell.setBackgroundResource(R.drawable.ic_notifications_active_24)


            } else {
                Log.d(TAG, "setUserProfile: notExist")
                unSignin.visibility = View.VISIBLE
                onSignin.visibility = View.GONE

                //mainViewModel._isExistAuthor.value = false
                //mainViewModel.getAccessToken()
                main.imageBtnBell.visibility = View.GONE
                mainViewModel.getAccessToken()
                mainViewModel.loadProfile()
                mainViewModel._notifications.value = null
                mainViewModel.loadNotifications()

            }
        })
    }

    // fragmentManager?.let { refreshFragment(this, it) }
    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        val ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()

    }
}