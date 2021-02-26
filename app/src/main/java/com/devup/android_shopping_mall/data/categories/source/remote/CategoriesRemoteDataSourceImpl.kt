package com.devup.android_shopping_mall.data.categories.source.remote

import com.devup.android_shopping_mall.data.categories.model.CategoriesRequest
import com.devup.android_shopping_mall.data.categories.model.CategoriesResponse
import com.devup.android_shopping_mall.data.categories.model.Category
import com.devup.android_shopping_mall.network.RetrofitInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class CategoriesRemoteDataSourceImpl(
    private val retrofitApiService: RetrofitInterface
) : CategoriesRemoteDataSource {


    override fun getCategories(
        request: CategoriesRequest,
        onSuccess: (response: List<Category>) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.getCategories(request.type).enqueue(object : Callback<List<Category>> {
            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(
                call: Call<List<Category>>,
                response: Response<List<Category>>
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


    override fun getCategoriesSearch(
        request: CategoriesRequest,
        onSuccess: (response: CategoriesResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    ) {
        retrofitApiService.getCategoriesSearch(request.type,request.search_word,request.page,request.limit).enqueue(object : Callback<CategoriesResponse> {
            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                onFailure(t)
            }

            override fun onResponse(
                call: Call<CategoriesResponse>,
                response: Response<CategoriesResponse>
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



}