package com.devup.android_shopping_mall.data.user.source.repository

import com.devup.android_shopping_mall.data.user.model.AccessToken
import com.devup.android_shopping_mall.data.user.model.SigninToken
import com.devup.android_shopping_mall.data.user.source.local.TokenLocalDataSource

class TokenRepositoryImpl(private val tokenLocalDataSource: TokenLocalDataSource) :
    TokenRepository {
    override fun getSigninToken(
        tokenExisted: (token: SigninToken) -> Unit,
        tokenNotExist: () -> Unit
    ) {
        tokenLocalDataSource.getSigninToken(tokenExisted, tokenNotExist)
    }

    override fun saveSigninToken(token: SigninToken) {
        tokenLocalDataSource.saveSigninToken(token)
    }

    override fun getAccessToken(
        accessTokenExisted: (token: AccessToken) -> Unit,
        accessTokenNotExist: () -> Unit
    ) {
        tokenLocalDataSource.getAccessToken(accessTokenExisted, accessTokenNotExist)
    }

    override fun saveAccessToken(token: AccessToken) {
        tokenLocalDataSource.saveAccessToken(token)
    }

    override fun deleteToken() {
        tokenLocalDataSource.deleteToken()
    }

}