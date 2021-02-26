package com.devup.android_shopping_mall.data.user.source.remote

import android.util.Log
import com.devup.android_shopping_mall.data.user.model.*
import com.devup.android_shopping_mall.network.NetworkCheck
import com.devup.android_shopping_mall.network.RetrofitInterface
import com.devup.android_shopping_mall.util.CANNOT_USE_NICKNAME
import com.devup.android_shopping_mall.util.CAN_USE_NICKNAME
import com.devup.android_shopping_mall.util.DIFFERENT_CURRENT_PASSWORD
import com.devup.android_shopping_mall.util.TOKEN_EXPIRED
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

/**
UserRemoteDataSource를 구현하는 구현체
 */

class UserRemoteDataSourceImpl(
    private val retrofitApiService: RetrofitInterface
) : UserRemoteDataSource {


    override fun getTerms(onSuccess: (Terms) -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.getTerms().enqueue(object : Callback<Terms> {
            override fun onFailure(call: Call<Terms>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<Terms>, response: Response<Terms>) {
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

    override fun checkUser(
        request: CheckUserRequest,
        onSuccess: (response: CheckUserResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.checkUser(request).enqueue(object : Callback<CheckUserResponse> {
            override fun onFailure(call: Call<CheckUserResponse>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(
                call: Call<CheckUserResponse>,
                response: Response<CheckUserResponse>
            ) {
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

    override fun checkNickname(request: CheckNicknameRequest, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.checkNickname(request).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.let { NetworkCheck.getErrorResponse(it) }.toString()
                    val errorCode = HttpException(response).code()
                    if (errorCode in 400..599) {
                        Log.e("checkNicknameResponse", errorBody)
                        when (errorBody) {
                            "ErrorResponse(code=not_found_profile_nickname)" -> notSuccessStatus(CAN_USE_NICKNAME)
                            "ErrorResponse(code=already_exist_profile_nickname)" -> notSuccessStatus(CANNOT_USE_NICKNAME)
                            else -> notSuccessStatus(0)
                        }
                        notSuccessStatus(errorCode)
                    }
                    onFailure(HttpException(response))
                }
            }

        })
    }

    override fun join(
        request: JoinRequest,
        onSuccess: (response: JoinResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.join(request).enqueue(object : Callback<JoinResponse> {
            override fun onFailure(call: Call<JoinResponse>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<JoinResponse>, response: Response<JoinResponse>) {
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

    override fun login(
        request: LoginRequest,
        onSuccess: (response: LoginResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val responseBody = response.body()
                if (responseBody != null && response.isSuccessful) {
                    onSuccess(responseBody)
                } else {
                    val errorBody = response.errorBody()?.let { NetworkCheck.getErrorResponse(it) }.toString()
                    val errorCode = HttpException(response).code()

                    if (errorCode == 401) {
                        Log.e("loginResponse", errorBody)
                        when (errorBody) {
                            "ErrorResponse(code=signin_token_expired)" -> notSuccessStatus(TOKEN_EXPIRED)
                            "ErrorResponse(code=access_token_expired)" -> notSuccessStatus(TOKEN_EXPIRED)
                            else -> notSuccessStatus(0)
                        }
                    } else notSuccessStatus(errorCode)

                    onFailure(HttpException(response))
                }
            }

        })
    }

    override fun logout(onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.logout().enqueue(object : Callback<ResponseBody> {
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

    override fun getUserInfo(
        onSuccess: (response: User) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.getUserInfo().enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
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

    override fun modifyUserInfo(request: ModifyUserInfoRequest, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.modifyUserInfo(request).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.let { NetworkCheck.getErrorResponse(it) }.toString()
                    val errorCode = HttpException(response).code()
                    if (errorCode in 400..599) {
                        Log.e("checkNicknameResponse", errorBody)
                        when (errorBody) {
                            "ErrorResponse(code=already_exist_profile_nickname)" -> notSuccessStatus(CANNOT_USE_NICKNAME)
                            else -> notSuccessStatus(0)
                        }
                        notSuccessStatus(errorCode)
                    }
                    onFailure(HttpException(response))
                }
            }

        })
    }

    override fun changePassword(request: ChangePasswordRequest, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.changePassword(request).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.let { NetworkCheck.getErrorResponse(it) }.toString()
                    val errorCode = HttpException(response).code()
                    if (errorCode in 400..599) {
                        Log.e("checkNicknameResponse", errorBody)
                        when (errorBody) {
                            "ErrorResponse(code=current_password_is_different)" -> notSuccessStatus(DIFFERENT_CURRENT_PASSWORD)
                            else -> notSuccessStatus(0)
                        }
                        notSuccessStatus(errorCode)
                    }
                    onFailure(HttpException(response))
                }
            }

        })
    }

    override fun findPassword(request: FindPasswordRequest, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.findPassword(request).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.let { NetworkCheck.getErrorResponse(it) }.toString()
                    val errorCode = HttpException(response).code()
                    if (errorCode in 400..599) {
                        Log.e("checkNicknameResponse", errorBody)
                        notSuccessStatus(errorCode)
                    }
                    onFailure(HttpException(response))
                }
            }

        })
    }

    override fun getOtherUserInfo(
        userId: Int,
        onSuccess: (response: UserInfo) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.getOtherUserInfo(userId).enqueue(object : Callback<UserInfo> {
            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
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

    override fun getOtherUserInfoAuth(userId: Int, onSuccess: (response: UserInfo) -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.getOtherUserInfoAuth(userId).enqueue(object : Callback<UserInfo> {
            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
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

    override fun addFollows(userId: Int, onSuccess: (response: FollowAddResponse) -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.addFollows(userId).enqueue(object : Callback<FollowAddResponse> {
            override fun onFailure(call: Call<FollowAddResponse>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<FollowAddResponse>, response: Response<FollowAddResponse>) {
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


    override fun deleteFollows(resource_id: Int, onSuccess: () -> Unit, notSuccessStatus: (errorCode: Int) -> Unit, onFailure: (e: Throwable) -> Unit) {
        retrofitApiService.deleteFollows(resource_id).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.let { NetworkCheck.getErrorResponse(it) }.toString()
                    val errorCode = HttpException(response).code()
                    if (errorCode in 400..599) {

                        notSuccessStatus(errorCode)
                    }
                    onFailure(HttpException(response))
                }
            }

        })
    }
}