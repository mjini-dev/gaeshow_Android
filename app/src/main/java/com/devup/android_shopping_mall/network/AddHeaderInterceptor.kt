package com.devup.android_shopping_mall.network

import com.devup.android_shopping_mall.data.user.source.repository.TokenRepository
import okhttp3.Interceptor
import okhttp3.Response

class AddHeaderInterceptor(val tokenRepository: TokenRepository) : Interceptor {
    lateinit var access_token: String

    init {
        getAccessToken()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        //저장된 accessToken을 가져온다

        val builder = chain.request().newBuilder()
        return chain.proceed(builder.apply {
            addHeader("Authorization", getAccessToken())
        }.build())
    }

    fun getAccessToken(): String {
        tokenRepository.getAccessToken(
            accessTokenExisted = { token ->
                access_token = token.access_token
            },
            accessTokenNotExist = {
                access_token = ""
            }
        )
        return access_token
    }

    fun setAccessToken(accessToken: String) {
        access_token = accessToken
    }
}