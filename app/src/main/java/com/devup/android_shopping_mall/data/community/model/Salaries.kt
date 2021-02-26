package com.devup.android_shopping_mall.data.community.model

data class Salaries(
    val name: String,
    val basic_salary: Int,
    val count: Int

    //TOp3 response
    ,
    val id:Int?,
    val laguages:List<Laguages>?

)