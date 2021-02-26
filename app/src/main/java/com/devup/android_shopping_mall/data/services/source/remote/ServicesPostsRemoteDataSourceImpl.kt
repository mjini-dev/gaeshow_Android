package com.devup.android_shopping_mall.data.services.source.remote

import android.util.Log
import com.devup.android_shopping_mall.data.services.model.ServicesPost
import com.devup.android_shopping_mall.data.services.model.ServicesPostsRequest
import com.devup.android_shopping_mall.data.services.model.ServicesPostsResponse
import com.devup.android_shopping_mall.network.NetworkCheck
import com.devup.android_shopping_mall.network.RetrofitInterface
import com.devup.android_shopping_mall.util.TOKEN_EXPIRED
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ServicesPostsRemoteDataSourceImpl(private val retrofitApiService: RetrofitInterface) : ServicesPostsRemoteDataSource {

    override fun getServicesPosts(
        request: ServicesPostsRequest,
        onSuccess: (response: ServicesPostsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.getCsPosts(request.category_id, request.page, request.limit, request.search_type, request.search_word)
            .enqueue(object : Callback<ServicesPostsResponse> {
                override fun onFailure(call: Call<ServicesPostsResponse>, t: Throwable) {
                    onFailure(t)
                }

                override fun onResponse(call: Call<ServicesPostsResponse>, response: Response<ServicesPostsResponse>) {
                    val responseBody = response.body()
                    if (responseBody != null && response.isSuccessful) {
                        onSuccess(responseBody)
                    } else {
                        val errorBody = response.errorBody()?.let { NetworkCheck.getErrorResponse(it) }.toString()
                        val errorCode = HttpException(response).code()

                        if (errorCode == 401) {
                            Log.e("loginResponse", errorBody)
                            when (errorBody) {
                                "ErrorResponse(code=access_token_expired)" -> notSuccessStatus(TOKEN_EXPIRED)
                                else -> notSuccessStatus(0)
                            }
                        } else notSuccessStatus(errorCode)

                        onFailure(HttpException(response))
                    }
                }

            })
    }

    override fun getServicesAuthPosts(
        request: ServicesPostsRequest,
        onSuccess: (response: ServicesPostsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.getCsAuthPosts(request.category_id, request.page, request.limit, request.search_type, request.search_word)
            .enqueue(object : Callback<ServicesPostsResponse> {
                override fun onFailure(call: Call<ServicesPostsResponse>, t: Throwable) {
                    onFailure(t)
                }

                override fun onResponse(call: Call<ServicesPostsResponse>, response: Response<ServicesPostsResponse>) {
                    val responseBody = response.body()
                    if (responseBody != null && response.isSuccessful) {
                        onSuccess(responseBody)
                    } else {
                        val errorBody = response.errorBody()?.let { NetworkCheck.getErrorResponse(it) }.toString()
                        val errorCode = HttpException(response).code()

                        if (errorCode == 401) {
                            Log.e("loginResponse", errorBody)
                            when (errorBody) {
                                "ErrorResponse(code=access_token_expired)" -> notSuccessStatus(TOKEN_EXPIRED)
                                else -> notSuccessStatus(0)
                            }
                        } else notSuccessStatus(errorCode)

                        onFailure(HttpException(response))
                    }
                }

            })
    }

    override fun getServicesPostDetails(
        resourceId: Int,
        onSuccess: (response: ServicesPost) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.getServicesPostDetails(resourceId).enqueue(object : Callback<ServicesPost> {
            override fun onFailure(call: Call<ServicesPost>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<ServicesPost>, response: Response<ServicesPost>) {
                val responseBody = response.body()
                if (responseBody != null && response.isSuccessful) {
                    onSuccess(responseBody)
                } else {
                    val errorBody = response.errorBody()?.let { NetworkCheck.getErrorResponse(it) }.toString()
                    val errorCode = HttpException(response).code()

                    if (errorCode == 401) {
                        Log.e("loginResponse", errorBody)
                        when (errorBody) {
                            "ErrorResponse(code=access_token_expired)" -> notSuccessStatus(TOKEN_EXPIRED)
                            else -> notSuccessStatus(0)
                        }
                    } else notSuccessStatus(errorCode)

                    onFailure(HttpException(response))
                }
            }

        })
    }
}