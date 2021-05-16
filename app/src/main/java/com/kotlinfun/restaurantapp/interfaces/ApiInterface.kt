package com.kotlinfun.restaurantapp.interfaces

import com.kotlinfun.restaurantapp.models.Model
import com.kotlinfun.restaurantapp.models.SelectedRestaurantModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {

    @get:GET("restaurants.json")
    val restaurants: Observable<Model>

    @GET("{id}/restaurant.json")
    fun menuItem(@Path("id") id: String): Observable<SelectedRestaurantModel>
}