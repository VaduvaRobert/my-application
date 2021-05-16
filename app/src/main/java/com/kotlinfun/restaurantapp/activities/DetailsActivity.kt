package com.kotlinfun.restaurantapp.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlinfun.restaurantapp.R
import com.kotlinfun.restaurantapp.adapters.MenuItemsAdapter
import com.kotlinfun.restaurantapp.interfaces.ApiInterface
import com.kotlinfun.restaurantapp.interfaces.RetrofitInstance
import com.kotlinfun.restaurantapp.models.MenuItemModel
import com.kotlinfun.restaurantapp.models.SelectedRestaurantModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.details_activity.*

class DetailsActivity : AppCompatActivity() {

    private lateinit var jsonApi: ApiInterface
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var menuItemsList: ArrayList<MenuItemModel>
    private lateinit var adapter: MenuItemsAdapter
    private lateinit var sortItemsArrayAdapter: ArrayAdapter<String>
    private var restaurantId: String? = null
    private var recycler_view: RecyclerView? = null
    private var sortItems: MutableList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_activity)
        //init api
        val retrofit = RetrofitInstance.instance
        jsonApi = retrofit.create(ApiInterface::class.java)
        val bundle :Bundle ?= intent.extras
        if (bundle != null){
            restaurantId = bundle.getString("restaurantId") // 1
        }
        menuItemsList = ArrayList()
        fetchMenuItems(restaurantId!!)
        //create menu adapter
        adapter = MenuItemsAdapter(this@DetailsActivity, menuItemsList)
        recycler_view = findViewById(R.id.rvMenuItems)
        recycler_view?.layoutManager = GridLayoutManager(this@DetailsActivity, 2)
        recycler_view?.adapter = adapter

        adapter.setOnClickListener(object : MenuItemsAdapter.ClickListener {
            override fun onClick(pos: Int, view: View) {
                Toast.makeText(this@DetailsActivity, menuItemsList.get(pos).title, Toast.LENGTH_SHORT).show()
            }
        })

        //create sort dropdown
        sortItems = resources.getStringArray(R.array.sortItems).toMutableList()
        sortItemsArrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, sortItems)
        atSortMenuItems.setAdapter(sortItemsArrayAdapter)
        atSortMenuItems.setOnItemClickListener { parent, view, position, id ->
            val select = parent.getItemAtPosition(position)
            when{
                select.equals("Ascending") -> { menuItemsList.sortBy { it.price }
                    adapter.notifyDataSetChanged()}
                select.equals("Descending") -> { menuItemsList.sortByDescending { it.price }
                    adapter.notifyDataSetChanged()}
            }
        }
        refreshApp()
    }

    fun refreshApp(){
        srMenuLayout.setOnRefreshListener {
            menuItemsList.clear()
            atSortMenuItems.setText(resources.getText(R.string.choose_text), false)
            adapter.notifyDataSetChanged()
            fetchMenuItems(restaurantId!!)
            srMenuLayout.isRefreshing = false
        }
    }

    //get menus list from server by restaurant id
    fun fetchMenuItems(restaurantId: String){
        compositeDisposable = CompositeDisposable()
        compositeDisposable.add(jsonApi.menuItem(restaurantId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { selectedRestaurantResponseModel -> displayMenuItems(selectedRestaurantResponseModel) },
                        { error -> showError(error.message) }
                )
        )
    }

    //show menus list
    fun displayMenuItems(data: SelectedRestaurantModel){
        tvMenuItemTitle.text = data.title
        menuItemsList.addAll(data.menu)
        adapter.notifyDataSetChanged()
    }

    fun showError(t: String?) {
        Toast.makeText(this, t, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}