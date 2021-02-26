package com.devup.android_shopping_mall.network

import com.devup.android_shopping_mall.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {

    /* single 블록 안에 초기화 코드 작성
    * Single instance만 제공
    * 매번 새로운 객체를 생성하고 싶다면 factory로 선언한다
    * 동일 타입을 리턴하는 경우 파라미터로 name을 설정한다
    *   single<name>*/

    single<Gson> { GsonBuilder().create() }

    single {
        OkHttpClient.Builder().apply {
            addInterceptor(get<AddHeaderInterceptor>())
            addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
        }.build()
    }

    single {
        AddHeaderInterceptor(get())
    }

//    single {
//        Interceptor { chain ->
//            chain.proceed(chain.request().newBuilder().apply {
//                header("Accept", "application/vnd.github.mercy-preview+json")
//            }.build())
//        }
//    }

    single {
        Retrofit.Builder()
            .baseUrl("")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(
            RetrofitInterface::class.java
        )
    }

}

