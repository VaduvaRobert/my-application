package com.kotlinfun.restaurantapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlinfun.restaurantapp.R
import com.kotlinfun.restaurantapp.activities.DetailsActivity
import com.kotlinfun.restaurantapp.models.RestaurantItemModel
import kotlinx.android.synthetic.main.restaurant_item_row.view.*

class RestaurantsAdapter (
        val context: Context,
        val restaurantsList : ArrayList<RestaurantItemModel>,
): RecyclerView.Adapter<RestaurantsAdapter.RestaurantViewHolder>() {

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        return RestaurantViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.restaurant_item_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurantsList[position]
        holder.itemView.animation =
                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.recycler_anim)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("restaurantId", restaurant.id.toString())
            context.applicationContext.startActivity(intent)
        }
        holder.itemView.apply {
            Glide.with(context)
                    .load(restaurant.image)
                    .into(ivRestaurant)
            tvRestaurantTitle.text = restaurant.title
            tvRestaurantSubtitle.text = restaurant.subtitle
        }
    }

    override fun getItemCount(): Int {
        return restaurantsList.size
    }

    fun switchList(list: ArrayList<RestaurantItemModel>){
        restaurantsList.clear()
        restaurantsList.addAll(list)
        notifyDataSetChanged()
    }
}