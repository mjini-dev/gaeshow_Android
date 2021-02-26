package com.devup.android_shopping_mall.data.user.source.local

import android.content.Context
import android.util.Log
import com.devup.android_shopping_mall.data.user.model.SimpleLoginUserProfile
import com.devup.android_shopping_mall.data.user.model.User
import com.devup.android_shopping_mall.data.user.model.UserProfile
import com.google.gson.GsonBuilder

class UserLocalDataSourceImpl(context: Context) : UserLocalDataSource {
    val TAG = "UserLocalDataSourceImpl"

    private val sharedPreferences =
        context.getSharedPreferences("UserLocalData", Context.MODE_PRIVATE)

    private fun buildToJson(user: User): String {
        return GsonBuilder().create().toJson(user)
    }

    override fun saveUserProfile(userProfile: User) {
        sharedPreferences.edit().putString("userInfo", buildToJson(userProfile)).apply()
        Log.d(TAG, "saveUserProfile ")
    }

    override fun getUserProfile(onProfileExist: (user: User) -> Unit, onProfileNotExist: () -> Unit) {
        val userInfo = sharedPreferences.getString("userInfo", null)
        if (userInfo != null) {
            onProfileExist(GsonBuilder().create().fromJson(userInfo,User::class.java))
        } else {
            onProfileNotExist()
        }
    }

   /* override fun getUserProfile(onProfileExist: (profile: UserProfile) -> Unit, onProfileNotExist: () -> Unit) {
        val userInfo = sharedPreferences.getString("userInfo", null)
        if (userInfo == null) {
            onProfileNotExist()
            return
        }
        val user = GsonBuilder().create().fromJson(userInfo, User::class.java)
        if (user != null) {
            onProfileExist(
                UserProfile(
                    user.user_id,
                    user.profile_image_url,
                    user.profile_nickname,
                    user.profile_email,
                    user.job_type,
                    user.job_field,
                    user.profile_gender,
                    user.experience_years,
                    user.working_area
                )
            )
        } else {
            onProfileNotExist()
        }
    }*/

    override fun saveSimpleLoginUserProfile(SimpleLoginUserProfile: SimpleLoginUserProfile) {
        sharedPreferences.edit().putString("SimpleLoginUserInfo", GsonBuilder().create().toJson(SimpleLoginUserProfile)).apply()
        Log.d(TAG, "saveSimpleLoginUserProfile: ${GsonBuilder().create().toJson(SimpleLoginUserProfile)}")
    }

    override fun getSimpleLoginUserProfile(onProfileExist: (SimpleLoginUserProfile: SimpleLoginUserProfile) -> Unit, onProfileNotExist: () -> Unit) {
        val kakaoUserProfile = sharedPreferences.getString("SimpleLoginUserInfo",null)
        if (kakaoUserProfile != null) {
            onProfileExist(GsonBuilder().create().fromJson(kakaoUserProfile,SimpleLoginUserProfile::class.java))
        } else {
            onProfileNotExist()
        }
    }


    override fun deleteProfile() {
        sharedPreferences.edit().clear().apply()
    }
}