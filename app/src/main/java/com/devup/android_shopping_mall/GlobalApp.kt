package com.devup.android_shopping_mall

import android.app.Application
import com.devup.android_shopping_mall.di.*
import com.devup.android_shopping_mall.network.networkModule
import com.kakao.sdk.common.KakaoSdk
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GlobalApp : Application() {

    override fun onCreate() {
        super.onCreate()

        //Kakao SDK를 사용하기 위해서 Native App Key로 초기화진행행
       KakaoSdk.init(this, "a53b7c931a7b7f217e75a7e448abeafa")

        //DI_Module Application 시작과 함께 startKoin을 통해 인자로 넘겨준다
        startKoin {
            androidContext(this@GlobalApp)
            modules(
                networkModule,
                userModule,
                tokenModule,
                deviceInfoModule,
                categoriesModule,
                postsModule,
                postModule,
                commentsModule,
                servicesPostModule,
                notificationModule,

                signupViewModelNodule,
                loginViewModelModule,
                boardViewModelModule,
                boardDetailsViewModelModule,
                boardWriteViewModelModule,
                moreFragmentViewModelModule,
                servicesViewModelModule,
                termsViewModelModule,
                myPageViewModelModule,
                mainViewModelModule,
                searchViewModelModule
            )
        }
    }
}