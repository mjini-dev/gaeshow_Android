package com.devup.android_shopping_mall.data.user.source.local

import android.content.Context
import com.devup.android_shopping_mall.data.user.model.AccessToken
import com.devup.android_shopping_mall.data.user.model.SigninToken

class TokenLocalDataSourceImpl(context: Context) : TokenLocalDataSource {
    //
    private val sharedPreferences =
        context.getSharedPreferences("TokenLocalData", Context.MODE_PRIVATE)

    override fun getSigninToken(
        tokenExisted: (token: SigninToken) -> Unit,
        tokenNotExist: () -> Unit
    ) {
        val token = sharedPreferences.getString("signin_token", null)
        if (token != null) {
            tokenExisted(SigninToken(token))
        } else {
            tokenNotExist()
        }
    }

    override fun saveSigninToken(token: SigninToken) {
        sharedPreferences.edit().putString("signin_token", token.signin_token).apply()
    }

    override fun getAccessToken(
        accessTokenExisted: (token: AccessToken) -> Unit,
        accessTokenNotExist: () -> Unit
    ) {
        val token = sharedPreferences.getString("access_token", null)
        if (token != null) {
            accessTokenExisted(AccessToken(token))
        } else {
            accessTokenNotExist()
        }
    }

    override fun saveAccessToken(token: AccessToken) {
        sharedPreferences.edit().putString("access_token", token.access_token).apply()
    }

    override fun deleteToken() {
        sharedPreferences.edit().clear().apply()
    }

}