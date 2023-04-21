package org.feup.apm.acme.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.feup.apm.acme.*
import org.feup.apm.acme.adaptors.ProductsAdapter
import org.feup.apm.acme.models.ProductAmount

class ShoppingCart : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.shoppingCartBackButton)}
    private val shoppingCartTotal by lazy { findViewById<TextView>(R.id.shoppingCartTotal)}
    private val navbar by lazy { findViewById<BottomNavigationView>(R.id.navbar) }
    private val mRecyclerView by lazy {findViewById<RecyclerView>(R.id.shoppingList)}
    private var products : ArrayList<ProductAmount> = arrayListOf()
    private var mAdapter: ProductsAdapter = ProductsAdapter(products)
    private val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
    private val checkOutButton by lazy {findViewById<Button>(R.id.shoppingCartCheckoutButton)}
    private val clearCart by lazy {findViewById<Button>(R.id.clearCart)}
    private var total = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)

        checkIfLoggedOut(this)

        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mAdapter

        seeProductsInCart()

        if (total == 0f){
            checkOutButton.isEnabled = false
        }

        checkOutButton.setOnClickListener {
            checkout()
        }

        clearCart.setOnClickListener {
            emptyShoppingCart(this)
            mAdapter.empty()
        }

        backButton.setOnClickListener {
            finish()
        }
        navBarListeners(navbar,this)
    }

    private fun checkout(){
        if (products.isEmpty()){
            createSnackBar("You need to have products in your cart to checkout",this)
        }
        val intent = Intent(this, CheckoutOptionsActivity::class.java)
        intent.putParcelableArrayListExtra("products",products)
        intent.putExtra("total",total)
        startActivity(intent)

    }

    private fun seeProductsInCart(){
        getCartProducts()

        mAdapter = ProductsAdapter(products)
        mRecyclerView.adapter = mAdapter

        mAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                getCartProducts()
                if (total == 0f){
                    checkOutButton.isEnabled = false
                }
            }
        })
    }


    private fun getCartProducts(){
        products = arrayListOf()
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