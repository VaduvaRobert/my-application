package com.kotlinfun.restaurantapp.interfaces

import com.kotlinfun.restaurantapp.util.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private var retrofitInstance: Retrofit? = null

    val instance: Retrofit
    get() {
        if (retrofitInstance == null) {
            retrofitInstance =  Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
        return retrofitInstance!!
    }
}