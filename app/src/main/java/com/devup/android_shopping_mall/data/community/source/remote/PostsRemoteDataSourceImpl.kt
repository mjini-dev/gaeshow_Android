package com.devup.android_shopping_mall.data.community.source.remote

import com.devup.android_shopping_mall.data.community.model.*
import com.devup.android_shopping_mall.network.NetworkCheck
import com.devup.android_shopping_mall.network.RetrofitInterface
import com.devup.android_shopping_mall.util.TOKEN_EXPIRED
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class PostsRemoteDataSourceImpl(private val retrofitApiService: RetrofitInterface) :
    PostsRemoteDataSource {

    override fun getPosts(
        request: PostsRequest,
        onSuccess: (response: PostsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.getPosts(request.category_id, request.page, request.limit, request.type, request.user_id, request.search_type, request.search_word)
            .enqueue(object : Callback<PostsResponse> {
                override fun onFailure(call: Call<PostsResponse>, t: Throwable) {
                    onFailure(t)
                }

                override fun onResponse(call: Call<PostsResponse>, response: Response<PostsResponse>) {
                    val responseBody = response.body()
                    if (responseBody != null && response.isSuccessful) {
                        onSuccess(responseBody)
                    } else {
                        val errorCode = HttpException(response).code()
                        if (errorCode in 400..599) {
                            notSuccessStatus(errorCode)
                        }
                        onFailure(HttpException(response))
                    }
                }

            })
    }

    override fun getPostDetails(
        resourceId: Int,
        onSuccess: (response: Post) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.getPostDetails(resourceId).enqueue(object : Callback<Post> {
            override fun onFailure(call: Call<Post>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
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

    override fun getPostDetailsAuth(resourceId: Int, onSuccess: (response: Post) -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.getPostDetailsAuth(resourceId).enqueue(object : Callback<Post> {
            override fun onFailure(call: Call<Post>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
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

    override fun getPostRecommendsDetails(
        type: String,
        unique_id: Int,
        onSuccess: (response: PostsRecommendsResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.getPostRecommendsDetails(type, unique_id).enqueue(object : Callback<PostsRecommendsResponse> {
            override fun onFailure(call: Call<PostsRecommendsResponse>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<PostsRecommendsResponse>, response: Response<PostsRecommendsResponse>) {
                val responseBody = response.body()
                if (responseBody != null && response.isSuccessful) {
                    onSuccess(responseBody)
                } else {
                    val errorCode = HttpException(response).code()
                    if (errorCode in 400..599) {
                        notSuccessStatus(errorCode)
                    }
                    onFailure(HttpException(response))
                }
            }

        })
    }

    override fun getPostSalariesDetails(
        job_filter: String,
        experience_years_filter: Int?,
        onSuccess: (response: PostsSalaryResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.getPostSalariesDetails(job_filter, experience_years_filter).enqueue(object : Callback<PostsSalaryResponse> {
            override fun onFailure(call: Call<PostsSalaryResponse>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<PostsSalaryResponse>, response: Response<PostsSalaryResponse>) {
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

    override fun getSalariesTop(
        onSuccess: (response: PostsSalaryTopResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.getSalariesTop().enqueue(object : Callback<PostsSalaryTopResponse> {
            override fun onFailure(call: Call<PostsSalaryTopResponse>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<PostsSalaryTopResponse>, response: Response<PostsSalaryTopResponse>) {
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

        }
        )
    }

}