package com.devup.android_shopping_mall.data.comments.source.remote

import android.util.Log
import com.devup.android_shopping_mall.data.comments.model.*
import com.devup.android_shopping_mall.data.source.remote.CommentsRemoteDataSource
import com.devup.android_shopping_mall.network.NetworkCheck
import com.devup.android_shopping_mall.network.RetrofitInterface
import com.devup.android_shopping_mall.util.TOKEN_EXPIRED
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response


class CommentsRemoteDataSourceImpl(private val retrofitApiService: RetrofitInterface) : CommentsRemoteDataSource {

    override fun getComments(
        resourceId: Int,
        //request: CommentsRequest,
        onSuccess: (response: Comments) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        //retrofitApiService.getComments(resourceId, request.page, request.limit)
        retrofitApiService.getComments(resourceId)
            .enqueue(object : Callback<Comments> {
                override fun onFailure(call: Call<Comments>, t: Throwable) {
                    onFailure(t)
                }

                override fun onResponse(call: Call<Comments>, response: Response<Comments>) {
                    val responseBody = response.body()
                    if (responseBody != null && response.isSuccessful) {
                        onSuccess(responseBody)
                    } else {
                        onFailure(HttpException(response))
                    }
                }
            })
    }

    override fun getCommentsAuth(resourceId: Int, onSuccess: (response: Comments) -> Unit, notSuccessStatus: (errorCode: Int) -> Unit,onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.getCommentsAuth(resourceId)
            .enqueue(object : Callback<Comments> {
                override fun onFailure(call: Call<Comments>, t: Throwable) {
                    onFailure(t)
                }

                override fun onResponse(call: Call<Comments>, response: Response<Comments>) {
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

    override fun getPostRecommendsComments(type: String, unique_id: Int, request: CommentsRequest, onSuccess: (response: Comments) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.getPostRecommendsComments(type, unique_id, request.page, request.limit).enqueue(object : Callback<Comments> {
            override fun onFailure(call: Call<Comments>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<Comments>, response: Response<Comments>) {
                val responseBody = response.body()
                if (responseBody != null && response.isSuccessful) {
                    onSuccess(responseBody)
                } else {
                    onFailure(HttpException(response))
                }
            }
        })
    }

    override fun addComment(
        resourceId: Int,
        request: AddCommentRequest,
        onSuccess: (response: AddCommentResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.addComment(resourceId, request).enqueue(object : Callback<AddCommentResponse> {
            override fun onFailure(call: Call<AddCommentResponse>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<AddCommentResponse>, response: Response<AddCommentResponse>) {
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

    override fun modifyComment(
        resourceId: Int,
        comment_id: Int,
        request: AddCommentRequest,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.modifyComment(resourceId,comment_id,request).enqueue(object : Callback<ResponseBody>{
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

    override fun deleteComment(resourceId: Int, comment_id: Int, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.deleteComment(resourceId,comment_id).enqueue(object : Callback<ResponseBody>{
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

    override fun addPostRecommendsComment(
        request: AddRecommendCommentRequest,
        onSuccess: (response: AddCommentResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.addPostRecommendsComment(request).enqueue(object : Callback<AddCommentResponse> {
            override fun onFailure(call: Call<AddCommentResponse>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<AddCommentResponse>, response: Response<AddCommentResponse>) {
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

    override fun deletePostRecommendsComment(comment_id: Int, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.deletePostRecommendsComment(comment_id).enqueue(object : Callback<ResponseBody>{
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

    override fun modifyPostRecommendsComment(
        comment_id: Int,
        request: AddRecommendCommentRequest,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.modifyPostRecommendsComment(comment_id,request).enqueue(object : Callback<ResponseBody>{
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


    override fun getServicesComments(
        resourceId: Int,
        onSuccess: (response: ServiceComments) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.getServicesComments(resourceId)
            .enqueue(object : Callback<ServiceComments> {
                override fun onFailure(call: Call<ServiceComments>, t: Throwable) {
                    onFailure(t)
                }

                override fun onResponse(call: Call<ServiceComments>, response: Response<ServiceComments>) {
                    val responseBody = response.body()
                    if (responseBody != null && response.isSuccessful) {
                        onSuccess(responseBody)
                    } else {
                        onFailure(HttpException(response))
                    }
                }
            })
    }

    override fun addServicesComment(
        resourceId: Int,
        request: AddCommentRequest,
        onSuccess: (response: AddCommentResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.addServicesComment(resourceId, request).enqueue(object : Callback<AddCommentResponse> {
            override fun onFailure(call: Call<AddCommentResponse>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<AddCommentResponse>, response: Response<AddCommentResponse>) {
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