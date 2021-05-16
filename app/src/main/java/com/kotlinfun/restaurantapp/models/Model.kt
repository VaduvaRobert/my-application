package com.kotlinfun.restaurantapp.models

data class Model (
        val restaurants: ArrayList<RestaurantItemModel>,
        val filters: ArrayList<FilterItemModel>
)