package com.kotlinfun.restaurantapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlinfun.restaurantapp.adapters.FiltersAdapter
import com.kotlinfun.restaurantapp.adapters.RestaurantsAdapter
import com.kotlinfun.restaurantapp.helpers.CustomProgressDialog
import com.kotlinfun.restaurantapp.interfaces.ApiInterface
import com.kotlinfun.restaurantapp.interfaces.RetrofitInstance
import com.kotlinfun.restaurantapp.models.FilterItemModel
import com.kotlinfun.restaurantapp.models.Model
import com.kotlinfun.restaurantapp.models.RestaurantItemModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var filtersAdapter: FiltersAdapter
    lateinit var filtersList: ArrayList<FilterItemModel>
    private lateinit var restaurantsAdapter: RestaurantsAdapter
    lateinit var restaurantsList: ArrayList<RestaurantItemModel>
    private lateinit var jsonApi: ApiInterface
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var sortedList: ArrayList<RestaurantItemModel>
    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        restaurantsList = ArrayList()
        filtersList = ArrayList()
        sortedList = ArrayList()

        //init api
        val retrofit = RetrofitInstance.instance
        jsonApi = retrofit.create(ApiInterface::class.java)
        fetchFiltersAndRestaurants()

        //create filter adapter
        filtersAdapter = FiltersAdapter(this, filtersList)
        rvFiltersList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false)
        rvFiltersList.adapter = filtersAdapter
        filtersAdapter.setOnClickListener(object: FiltersAdapter.ClickListener{
            override fun onClick(pos: Int, view: View) {
                if (filtersAdapter.index != pos) {
                    filtersAdapter.index = pos
                    val restaurantName = filtersList[pos].label + " Restaurant"
                    tvRestaurantType.text = restaurantName
                } else {
                    tvRestaurantType.text = resources.getText(R.string.all_restaurants_text)
                    restaurantsList.clear()
                    filtersAdapter.index = -1
                    fetchRestaurants()
                }

                filtersAdapter.notifyDataSetChanged()
                val selectedOption = filtersList.get(pos).id
                sortedList.clear()

                    for(element in restaurantsList){
                        for(el in element.filters) {
                            if (el.contains(selectedOption)) {
                                sortedList.addAll(listOf(element))
                            }
                        }
                    }
                restaurantsAdapter.switchList(sortedList)
                }
        })

        //create restaurant adapter
        restaurantsAdapter = RestaurantsAdapter(this, ArrayList())
        rvRestaurantsList.layoutManager = LinearLayoutManager(this)
        rvRestaurantsList.adapter = restaurantsAdapter

        refreshApp()
    }

    fun refreshApp(){
        srLayout.setOnRefreshListener {
            tvRestaurantType.text = resources.getText(R.string.all_restaurants_text)
            restaurantsList.clear()
            filtersAdapter.index = -1
            filtersAdapter.notifyDataSetChanged()
            restaurantsAdapter.notifyDataSetChanged()
            fetchRestaurants()
            srLayout.isRefreshing = false
        }
    }

    //get data from server
    fun fetchFiltersAndRestaurants(){
        progressDialog.show(this, resources.getText(R.string.loading_message_text))
         compositeDisposable = CompositeDisposable()
         compositeDisposable.add(jsonApi.restaurants
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(
                 { dataModel -> displayFiltersAndRestaurants(dataModel) },
                 { error -> showError(error.message) }
             )
         )
    }

    //show data
    fun displayFiltersAndRestaurants(data: Model){
        progressDialog.dialog.dismiss()
        filtersList.addAll(data.filters)
        restaurantsList.addAll(data.restaurants)
        filtersAdapter.notifyDataSetChanged()
        restaurantsAdapter.switchList(restaurantsList)
        restaurantsAdapter.notifyDataSetChanged()
    }

    //get restaurants list from server after refresh
    fun fetchRestaurants(){
        compositeDisposable = CompositeDisposable()
        compositeDisposable.add(jsonApi.restaurants
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { dataModel -> displayRestaurants(dataModel) },
                        { error -> showError(error.message) }
                )
        )
    }

    //show restaurants list from server after refresh
    fun displayRestaurants(data: Model){
        restaurantsList.addAll(data.restaurants)
        restaurantsAdapter.switchList(restaurantsList)
        restaurantsAdapter.notifyDataSetChanged()
    }

    fun showError(t: String?) {
        Toast.makeText(this, t, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}