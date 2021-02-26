package com.devup.android_shopping_mall.data.user.source.local

import com.devup.android_shopping_mall.data.user.model.AccessToken
import com.devup.android_shopping_mall.data.user.model.SigninToken

interface TokenLocalDataSource {

    fun getSigninToken(
        tokenExisted: (token: SigninToken) -> Unit,
        tokenNotExist: () -> Unit
    )

    fun saveSigninToken(token: SigninToken)

    fun getAccessToken(
        accessTokenExisted: (token: AccessToken) -> Unit,
        accessTokenNotExist: () -> Unit
    )

    fun saveAccessToken(token: AccessToken)

    fun deleteToken()

}