package com.devup.android_shopping_mall.network


import com.devup.android_shopping_mall.data.error.ErrorResponse
import okhttp3.ResponseBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.HttpException
import retrofit2.Retrofit

object NetworkCheck : KoinComponent {

    private val retrofit: Retrofit by inject()
    private val addHeaderInterceptor: AddHeaderInterceptor by inject()

    fun getErrorResponse(errorBody: ResponseBody): ErrorResponse {
        return retrofit.responseBodyConverter<ErrorResponse>(
            ErrorResponse::class.java,
            ErrorResponse::class.java.annotations
        ).convert(errorBody) ?: ErrorResponse("")
    }

    fun getErrorMessage(e: Throwable): String {
        return (if (e is HttpException) {
            val errorBody = e.response()?.errorBody()
            if (errorBody != null) {
                "[ErrorBody] ${e.code()} , ${e.message()} , ${getErrorResponse(errorBody)}"
            } else {
                "[HttpExceptionError] ${e.code()} , ${e.message()}"
            }
        } else {
            e.toString()
        })
    }

    fun setAccessToken(accessToken: String) {
        addHeaderInterceptor.setAccessToken(accessToken)
    }


}