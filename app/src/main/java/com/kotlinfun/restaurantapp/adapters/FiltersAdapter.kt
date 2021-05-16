package com.kotlinfun.restaurantapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.kotlinfun.restaurantapp.R
import com.kotlinfun.restaurantapp.models.FilterItemModel
import kotlinx.android.synthetic.main.filter_item_row.view.*

class FiltersAdapter(
        val context: Context,
        val filtersList: ArrayList<FilterItemModel>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var index = -1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    (holder as FiltersAdapter.FilterViewHolder).bind(filtersList.get(position))
        if(index == position) {
            holder.itemView.selectedLine.visibility = View.VISIBLE
        } else {
            holder.itemView.selectedLine.visibility = View.INVISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInfalter = LayoutInflater.from(context)
        return FilterViewHolder(layoutInfalter.inflate(R.layout.filter_item_row, parent, false))
    }

    override fun getItemCount(): Int {
        return filtersList.size
    }

    lateinit var mClickListener: ClickListener

    fun setOnClickListener(aClickListener: ClickListener){
        mClickListener = aClickListener
    }

    interface ClickListener {
        fun onClick(pos: Int, view: View)
    }

    inner class FilterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(model: FilterItemModel){
            itemView.ivFilterImage.loadUrl(model.icon)
            itemView.tvFilterTitle.text = model.label
        }

        override fun onClick(v: View?) {
            mClickListener.onClick(adapterPosition, itemView)
        }

        fun ImageView.loadUrl(url: String) {

            val imageLoader = ImageLoader.Builder(this.context)
                .componentRegistry { add(SvgDecoder(context)) }
                .build()

            val circularProgressDrawable = CircularProgressDrawable(this.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            val request = ImageRequest.Builder(this.context)
                .crossfade(true)
                .crossfade(100)
                .placeholder(circularProgressDrawable)
                .error(R.drawable.ic_launcher_foreground)
                .data(url)
                .target(this)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .build()

            imageLoader.enqueue(request)
        }
    }
}