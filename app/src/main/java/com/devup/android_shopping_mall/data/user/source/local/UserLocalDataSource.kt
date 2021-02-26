package com.devup.android_shopping_mall.data.user.source.local

import com.devup.android_shopping_mall.data.user.model.SimpleLoginUserProfile
import com.devup.android_shopping_mall.data.user.model.User
import com.devup.android_shopping_mall.data.user.model.UserProfile

interface UserLocalDataSource {

    fun saveUserProfile(user: User)

    fun getUserProfile(
        onProfileExist: (user: User) -> Unit,
        onProfileNotExist: () -> Unit
    )

    //moreFrag에서 보여줄 간단정보
    /*fun getUserProfile(
        onProfileExist: (profile: UserProfile) -> Unit,
        onProfileNotExist: () -> Unit
    )*/

    fun saveSimpleLoginUserProfile(SimpleLoginUserProfile: SimpleLoginUserProfile)

    fun getSimpleLoginUserProfile(
        onProfileExist: (SimpleLoginUserProfile: SimpleLoginUserProfile) -> Unit,
        onProfileNotExist: () -> Unit
    )

    fun deleteProfile()


}