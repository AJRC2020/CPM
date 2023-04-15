package org.feup.apm.acme.activities

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.feup.apm.acme.R
import org.feup.apm.acme.adaptors.ProductsAdapter
import org.feup.apm.acme.convertToEuros
import org.feup.apm.acme.models.ProductAmount
import org.feup.apm.acme.navBarListeners

class ShoppingCart : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.shoppingCartBackButton)}
    private val shoppingCartTotal by lazy { findViewById<TextView>(R.id.shoppingCartTotal)}
    private val navbar by lazy { findViewById<BottomNavigationView>(R.id.navbar) }
    private val mRecyclerView by lazy {findViewById<RecyclerView>(R.id.shoppingList)}
    private var mAdapter: ProductsAdapter = ProductsAdapter(mutableListOf())
    private val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

    private var products : MutableList<ProductAmount> = mutableListOf()
    private var total = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)

        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mAdapter

        seeProductsInCart()
        //Buttons
        backButton.setOnClickListener {
            finish()
        }
        navBarListeners(navbar,this)
    }

    private fun seeProductsInCart(){
        getCartProducts()
        mAdapter = ProductsAdapter(products)
        mRecyclerView.adapter = mAdapter

        mAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                getCartProducts()
            }
        })
    }

    private fun getCartProducts(){
        products = mutableListOf()
        val sharedPreference = this.getSharedPreferences("shopping_cart_prod_names", Context.MODE_PRIVATE)
        val sharedPreferencePrices = this.getSharedPreferences("shopping_cart_prod_prices", Context.MODE_PRIVATE)
        val sharedPreferenceAmount = this.getSharedPreferences("shopping_cart_prod_amount", Context.MODE_PRIVATE)

        val allEntries: Map<String, *> = sharedPreference.all
        for ((key, value) in allEntries) {
            val price = sharedPreferencePrices.getFloat(key,0f)
            val amount = sharedPreferenceAmount.getInt(key,0)
            val product = ProductAmount(key,amount,value.toString(),price)
            products.add(product)
        }
        getTotal()
    }


    private fun getTotal(){
        total = 0f
        products.forEach{ product ->
            total += product.amount * product.price
        }

        shoppingCartTotal.text = convertToEuros(total)
    }


}