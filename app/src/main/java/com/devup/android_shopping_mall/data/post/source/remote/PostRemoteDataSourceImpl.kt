package com.devup.android_shopping_mall.data.post.source.remote

import com.devup.android_shopping_mall.data.post.model.*
import com.devup.android_shopping_mall.data.uploadfile.UploadFileResponse
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

class PostRemoteDataSourceImpl(private val retrofitApiService: RetrofitInterface) :
    PostRemoteDataSource {


    override fun addPost(
        request: PostAddRequest,
        onSuccess: (response: PostAddResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.addPost(request)
            .enqueue(object : Callback<PostAddResponse> {
                override fun onFailure(call: Call<PostAddResponse>, t: Throwable) {
                    onFailure(t)
                }

                override fun onResponse(call: Call<PostAddResponse>, response: Response<PostAddResponse>) {
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


    override fun addServicesPost(
        request: ServicesPostAddRequest,
        onSuccess: (response: PostAddResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.addServicesPost(request)
            .enqueue(object : Callback<PostAddResponse> {
                override fun onFailure(call: Call<PostAddResponse>, t: Throwable) {
                    onFailure(t)
                }

                override fun onResponse(call: Call<PostAddResponse>, response: Response<PostAddResponse>) {
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

    override fun uploadFile(
        request: RequestBody,
        file: MultipartBody.Part,
        onSuccess: (response: List<UploadFileResponse>) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.uploadFile(request, file).enqueue(object : Callback<List<UploadFileResponse>> {
            override fun onFailure(call: Call<List<UploadFileResponse>>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<List<UploadFileResponse>>, response: Response<List<UploadFileResponse>>) {
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

    override fun modifyPost(resourceId: Int, request: PostAddRequest, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.modifyPost(resourceId, request).enqueue(object : Callback<ResponseBody> {
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

    override fun deletePost(resourceId: Int, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.deletePost(resourceId).enqueue(object : Callback<ResponseBody> {
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

    override fun ratingsAddPost(
        request: PostRatingsAddRequest,
        onSuccess: (response: PostRatingsAddResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.ratingsAddPost(request).enqueue(object : Callback<PostRatingsAddResponse> {
            override fun onFailure(call: Call<PostRatingsAddResponse>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<PostRatingsAddResponse>, response: Response<PostRatingsAddResponse>) {
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

    override fun ratingsDeletePost(resourceId: Int, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.ratingsDeletePost(resourceId).enqueue(object : Callback<ResponseBody> {
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

    override fun bookmarksAddPost(
        request: PostBookmarksAddRequest,
        onSuccess: (response: PostBookmarksAddResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.bookmarksAddPost(request).enqueue(object : Callback<PostBookmarksAddResponse> {
            override fun onFailure(call: Call<PostBookmarksAddResponse>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<PostBookmarksAddResponse>, response: Response<PostBookmarksAddResponse>) {
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

    override fun bookmarksDeletePost(resourceId: Int, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.bookmarksDeletePost(resourceId).enqueue(object : Callback<ResponseBody> {
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