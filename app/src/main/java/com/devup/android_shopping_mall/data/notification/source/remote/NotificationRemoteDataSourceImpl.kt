package com.devup.android_shopping_mall.data.notification.source.remote

import com.devup.android_shopping_mall.data.notification.model.NotificationsResponse
import com.devup.android_shopping_mall.data.post.model.*
import com.devup.android_shopping_mall.data.uploadfile.UploadFileResponse
import com.devup.android_shopping_mall.data.user.model.User
import com.devup.android_shopping_mall.network.NetworkCheck
import com.devup.android_shopping_mall.network.RetrofitInterface
import com.devup.android_shopping_mall.util.TOKEN_EXPIRED
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class NotificationRemoteDataSourceImpl(private val retrofitApiService: RetrofitInterface) :
    NotificationRemoteDataSource {

    override fun getNotifications(
        page: Int,
        limit: Int,
        onSuccess: (response: NotificationsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.getNotifications(page, limit).enqueue(object : Callback<NotificationsResponse> {
            override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<NotificationsResponse>, response: Response<NotificationsResponse>) {
                val responseBody = response.body()
                if (responseBody != null && response.isSuccessful) {
                    onSuccess(responseBody)
                } else {
                    val errorBody = response.errorBody()?.let { NetworkCheck.getErrorResponse(it) }.toString()
                    val errorCode = HttpException(response).code()
                    if (errorCode == 401) {
                        when (errorBody) {
                            "ErrorResponse(code=access_token_expired)" -> notSuccessStatus(TOKEN_EXPIRED)
                            "ErrorResponse(code=No Authorization headers)" -> notSuccessStatus(TOKEN_EXPIRED)
                            else -> notSuccessStatus(0)
                        }
                    } else notSuccessStatus(errorCode)

                    onFailure(HttpException(response))
                }
            }

        })
    }

    override fun modifyNotifications(resource_id: Int, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.modifyNotifications(resource_id).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.let { NetworkCheck.getErrorResponse(it) }.toString()
                    val errorCode = HttpException(response).code()
                    if (errorCode == 401) {
                        when (errorBody) {
                            "ErrorResponse(code=access_token_expired)" -> notSuccessStatus(TOKEN_EXPIRED)
                            "ErrorResponse(code=No Authorization headers)" -> notSuccessStatus(TOKEN_EXPIRED)
                            else -> notSuccessStatus(0)
                        }
                    } else notSuccessStatus(errorCode)

                    onFailure(HttpException(response))
                }
            }
        })
    }
}