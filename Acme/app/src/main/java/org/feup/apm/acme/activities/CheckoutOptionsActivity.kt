package org.feup.apm.acme.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.feup.apm.acme.*
import org.feup.apm.acme.adaptors.ProductsAdapter
import org.feup.apm.acme.models.ProductAmount
import org.feup.apm.acme.models.Voucher
import kotlin.concurrent.thread


class CheckoutOptionsActivity : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.checkOutOpBackButton)}
    private val navbar by lazy { findViewById<BottomNavigationView>(R.id.navbar) }
    private val mRecyclerView by lazy {findViewById<RecyclerView>(R.id.list_op)}
    private var mAdapter: ProductsAdapter = ProductsAdapter(arrayListOf())
    private val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
    private val totalView by lazy { findViewById<TextView>(R.id.total_op) }
    private var products: ArrayList<ProductAmount> = arrayListOf()
    private var total = 0f
    private val confirmCheckout  by lazy { findViewById<TextView>(R.id.confirmCheckout) }
    private val progressBar by lazy{findViewById<ProgressBar>(R.id.progressBar7)}
    private val accAmountField by lazy{findViewById<TextView>(R.id.accAmountVal)}
    private val vouchersDropDown by lazy{findViewById<Spinner>(R.id.vouchersDropDown)}
    private val checkBox by lazy{findViewById<CheckBox>(R.id.checkBox)}
    private var vouchers = arrayListOf("None")
    private var accAmount = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_options)
        checkIfLoggedOut(this)

        getIntentInfo()
        setUpScreen()

        getOptions()


        setUpCheckBox()
        setUpDropDown()


        confirmCheckout.setOnClickListener{
            confirmCheckout()
        }

        //Buttons
        backButton.setOnClickListener {
            finish()
        }
        navBarListeners(navbar,this)
    }

    private fun getIntentInfo(){
        val intent = intent
        val prods: ArrayList<ProductAmount>? = intent.getParcelableArrayListExtra("products")
        if (prods === null){
            createSnackBar("Error retrieving products, please retry.",this)
        }else{
            products = prods
        }
        total = intent.getFloatExtra("total",-1f)
    }

    private fun confirmCheckout(){
        val voucher = vouchersDropDown.selectedItem.toString()
        val acc = checkBox.isSelected
        val intent = Intent(this, CheckoutActivity::class.java)
        intent.putExtra("voucher",voucher)
        intent.putExtra("useAcc",acc)
        intent.putParcelableArrayListExtra("products",products)
        startActivity(intent)

    }

    private fun setUpScreen(){
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = ProductsAdapter(products)
        mRecyclerView.adapter = mAdapter

        totalView.text = convertToEuros(total)
    }

    private fun setUpDropDown(){
        val arrayAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,vouchers)
        vouchersDropDown.adapter = arrayAdapter
    }

    private fun setUpCheckBox(){
        accAmountField.text = convertToEuros(accAmount)
        if (accAmount == 0f){
            checkBox.isEnabled = false
        }
    }

    private fun getOptions(){
        loading(progressBar, listOf(confirmCheckout))
        var allVouchers: ArrayList<Voucher>
        vouchers = arrayListOf("None")
        val sharedPreference = this.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val uuid = sharedPreference.getString("uuid", "none")
        val username = sharedPreference.getString("username", "none").toString()

        thread {
            val vouchersInfo = uuid?.let {
                getVouchers(
                    this,
                    it,username
                )
            }!!
            allVouchers = vouchersInfo.vouchers

            this.runOnUiThread {
                for (voucher in allVouchers) {
                    if (!voucher.used && voucher.emitted){
                        vouchers.add(voucher.uuid)
                    }
                }
                getAccAmount(uuid,username,sharedPreference)
            }
        }
    }

    private fun getAccAmount(uuid: String, username: String, sharedPreference: SharedPreferences){
        thread{
            getUserInfo(
                this,
                uuid,username
            )
            this.runOnUiThread {
                accAmount = sharedPreference.getFloat("total",0f)
                stopLoading(progressBar,listOf(confirmCheckout))
            }
        }
    }





}