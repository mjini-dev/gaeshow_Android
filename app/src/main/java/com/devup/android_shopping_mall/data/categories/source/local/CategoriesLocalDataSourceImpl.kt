package com.devup.android_shopping_mall.data.categories.source.local

import android.content.Context
import com.devup.android_shopping_mall.data.categories.model.CategoriesResponse
import com.devup.android_shopping_mall.data.categories.model.Category
import com.google.gson.GsonBuilder

class CategoriesLocalDataSourceImpl(context: Context) : CategoriesLocalDataSource {

    private val sharedPreferences =
        context.getSharedPreferences("CategoriesLocalData", Context.MODE_PRIVATE)

    private fun buildToJson(categories: List<Category>): String {
        return GsonBuilder().create().toJson(CategoriesResponse(null,null,categories))
    }

    /**커뮤니티에 필요한 카테고리*/
    override fun savePostCategories(postCategories: List<Category>) {
        sharedPreferences.edit().putString("postCategories", buildToJson(postCategories)).apply()
    }

    override fun getPostCategories(
        onSuccess: (postCategories: List<Category>) -> Unit,
        onFailure: () -> Unit
    ) {
        val categories = sharedPreferences.getString("postCategories", null)
        if (categories != null) {
            onSuccess(GsonBuilder().create().fromJson(categories, CategoriesResponse::class.java).categories)
        } else {
            onFailure()
        }
    }

    /**고객센터에 필요한 카테고리*/
    override fun saveServiceCategories(serviceCategories: List<Category>) {
        sharedPreferences.edit().putString("serviceCategories", buildToJson(serviceCategories)).apply()
    }

    override fun getServiceCategories(
        onSuccess: (serviceCategories: List<Category>) -> Unit,
        onFailure: () -> Unit
    ) {
        val categories = sharedPreferences.getString("serviceCategories", null)
        if (categories != null) {
            onSuccess(GsonBuilder().create().fromJson(categories, CategoriesResponse::class.java).categories)
        } else {
            onFailure()
        }
    }

    /**회원가입시 입력해야할 job_type, job_field의 종류를 볼 수 있는 카테고리*/
    override fun saveJobCategories(jobCategories: List<Category>) {
        sharedPreferences.edit().putString("jobCategories", buildToJson(jobCategories)).apply()
    }

    override fun getJobCategories(onSuccess: (jobCategories: List<Category>) -> Unit, onFailure: () -> Unit) {
        val categories = sharedPreferences.getString("jobCategories", null)
        if (categories != null) {
            onSuccess(GsonBuilder().create().fromJson(categories, CategoriesResponse::class.java).categories)
        } else {
            onFailure()
        }
    }

    /**ide 조회시 필요한 카테고리*/
    override fun saveIdeCategories(ideCategories: List<Category>) {
        sharedPreferences.edit().putString("ideCategories", buildToJson(ideCategories)).apply()
    }

    override fun getIdeCategories(onSuccess: (ideCategories: List<Category>) -> Unit, onFailure: () -> Unit) {
        val categories = sharedPreferences.getString("ideCategories", null)
        if (categories != null) {
            onSuccess(GsonBuilder().create().fromJson(categories, CategoriesResponse::class.java).categories)
        } else {
            onFailure()
        }
    }

    /**language 조회시 필요한 카테고리*/
    override fun saveLanguageCategories(languageCategories: List<Category>) {
        sharedPreferences.edit().putString("languageCategories", buildToJson(languageCategories)).apply()
    }

    override fun getLanguageCategories(onSuccess: (languageCategories: List<Category>) -> Unit, onFailure: () -> Unit) {
        val categories = sharedPreferences.getString("languageCategories", null)
        if (categories != null) {
            onSuccess(GsonBuilder().create().fromJson(categories, CategoriesResponse::class.java).categories)
        } else {
            onFailure()
        }
    }


}