package com.devup.android_shopping_mall.view.login

import androidx.lifecycle.ViewModel
import com.devup.android_shopping_mall.data.categories.source.repository.CategoriesRepository
import com.devup.android_shopping_mall.data.deviceInfo.repository.DeviceInfoRepository
import com.devup.android_shopping_mall.data.user.source.repository.TokenRepository
import com.devup.android_shopping_mall.data.user.source.repository.UserRepository

class LoginViewModel(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
    private val deviceInfoRepository: DeviceInfoRepository,
    private val categoriesRepository: CategoriesRepository

) : ViewModel() {
    private val TAG: String = "LoginViewModel"

    /* val loginLiveData = MutableLiveData<Boolean>()
     val access_token = MutableLiveData<String>()

     fun login(loginRequest: login_request) {
         viewModelScope.launch {
             try {
                 val loginResponse = apiService.login(loginRequest)
                 access_token.value = loginResponse.access_token

             } catch (e: Throwable) {
                 loginLiveData.value = false
                 Log.e(TAG, "error of viewModelScope.launch $e")


             }
         }
     }

     val errorBody = MutableLiveData<String>()
     val responsCode = MutableLiveData<Int>()

     fun loginCallback(loginRequest: login_request) {
         apiService.login_cb(loginRequest).enqueue(object : Callback<signin> {

             override fun onResponse(call: Call<signin>, response: Response<signin>) {

                 val responsErrorBody = response.errorBody()?.let { getErrorResponse(it) }
                 responsCode.value = response.code()
                 Log.e(TAG, "response.code: ${response.code()}")
                 Log.e(TAG, "responsCode.value: ${responsCode.value}")

                 if (response.isSuccessful) {

                 } else {
                     errorBody.value = responsErrorBody.toString()
                     Log.e(TAG, "response.errorBody: $responsErrorBody")
                 }
             }

             override fun onFailure(call: Call<signin>, t: Throwable) {
                 Log.e(TAG, "t : $t")
             }

         })
     }
 */
}