package com.devup.android_shopping_mall.view.home


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    //private val categoryViewModel: CategoryViewModel by viewModels()

    private val viewModel: MainViewModel by inject()

    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        val binding : ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@MainActivity

            viewModel.getAccessToken()
            viewModel.loadNotifications()
        }

        //Toolbar 셋팅
        toolbar = findViewById<Toolbar>(R.id.toolbarMain)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        // Bottom Navigaton 셋팅
        navController = findNavController(R.id.fragmentMainFrameContainerView)
        menuBottomNavi.setupWithNavController(navController)

        //navigationView 메뉴(필요)
        navigationView = findViewById<NavigationView>(R.id.navigationView)


        //메뉴 클릭이벤트
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        //드로어 네비게이션 아이테을 클릭하면, 네비컨트롤러_navGraph대로 프래그먼트가 전환된다
        NavigationUI.setupWithNavController(navigationView, navController)

        /*
        //네비게이션(nav_gragh)에 label이 타이틀로 온다
        //가장 상위프래그먼트(app:startDestination="@id/navigation_home)에서는 햄버거버튼이 보여지고
        //나머지 프래그먼트에서는 뒤로가기(<-)버튼이 생성되며 클릭시 상위프래그먼트로 이동한다
        //setupActionBarWithNavController(navController, appBarConfiguration)

        //위의 설정을 툴바에 적용하는것
        //findViewById<Toolbar>(R.id.toolbar_main).setupWithNavController(navController, appBarConfiguration)
        */
//-----
        //Drawer Swipe 잠금
        //다른프래그먼트에서 swipe했을때 드로어네비게이션이 나오지 않도록
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)


        //네비게이션 메뉴
        val menu: Menu = navigationView.menu
        val categoryId: View = layoutInflater.inflate(R.layout.menu_category_id, null, false)


        /*//알림클릭
        imageBtnBell.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentMainFrameContainerView, NotificationsFragment()).commit()
        }*/
        observeExistAuthor()

    }

    private fun observeExistAuthor() {
        viewModel.isExistAuthor.observe(this, Observer { exist ->
            if (exist) {
                imageBtnBell.visibility = View.VISIBLE
            } else imageBtnBell.visibility = View.GONE
        })
    }
}