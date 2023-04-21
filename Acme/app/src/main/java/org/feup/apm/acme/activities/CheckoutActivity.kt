package org.feup.apm.acme.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.feup.apm.acme.*
import org.feup.apm.acme.fragments.DialogGeneric
import org.feup.apm.acme.models.ProductAmount
import org.json.JSONArray
import org.json.JSONObject
import kotlin.concurrent.thread

class CheckoutActivity : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.checkOuttBackButton)}
    private val navbar by lazy { findViewById<BottomNavigationView>(R.id.navbar) }
    private val qrCode by lazy { findViewById<ImageView>(R.id.qrCodePos) }
    private val cancelBut by lazy {findViewById<Button>(R.id.cancelPurchase)}
    private var products = arrayListOf<ProductAmount>()
    private var useAcc = false
    private var voucher = "None"
    private var handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        checkIfLoggedOut(this)
        getIntentInfo()

        val sharedPreference = this.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val uuid = sharedPreference.getString("uuid","none")
        val username = sharedPreference.getString("username","none").toString()

        val message = uuid?.let { buildMessage(it,username) }

        thread {
            if (message != null) {
                encodeAsBitmap(message,this).also { runOnUiThread { qrCode.setImageBitmap(it) } }
            }

            runOnUiThread{
                if (uuid != null) {
                    requestPeriodically(uuid,username)
                }
            }
        }

        // Destroying handler so it doesn't keep running in the bg forever
        backButton.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            finish()
        }

        cancelBut.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handler.removeCallbacksAndMessages(null)
                finish()
            }
        })

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
        useAcc = intent.getBooleanExtra("useAcc",false)
        Log.d("useAcc",useAcc.toString())
        voucher = intent.getStringExtra("voucher") ?: "None"
    }

    private fun requestPeriodically(uuid:String,username: String){
        val delay = 100
        var stop = false

        handler.postDelayed(object : Runnable {
            override fun run() {
                thread {
                    try {
                        val receipts = getJustEmittedPurchases(uuid, username)
                        receipts.forEach {
                            if (it.items == products &&
                                (it.voucher == voucher || (it.voucher == "null" && voucher == "None"))
                            ) {
                                endPurchase()
                                stop = true
                                return@forEach
                            }
                        }
                        if (!stop) {
                            handler.postDelayed(this, delay.toLong())
                        }
                    }catch (e: Exception){
                        val dialog = e.message?.let { DialogGeneric("Error", it) }
                        dialog?.show(supportFragmentManager, "error")
                    }
                }
            }
        }, delay.toLong())
    }


    private fun endPurchase(){
        emptyShoppingCart(this)
        val intent = Intent(this, PurchaseResponseActivity::class.java)
        intent.putExtra("message","Purchase completed!")
        startActivity(intent)
    }

    private fun buildMessage(uuid: String, username: String): String{

        val message = JSONObject()
        if (voucher != "None"){
            message.put("voucher_id",voucher)
        }else{
            message.put("voucher_id",JSONObject.NULL)
        }
        message.put("discount",useAcc)
        message.put("user_id",uuid)
        val productsJson : ArrayList<JSONObject> = arrayListOf()
        for (product in products) {
            val prodJ = JSONObject()
            prodJ.put("quantity",product.amount)
            prodJ.put("product",product.uuid)
            productsJson.add(prodJ)
        }
        message.put("products",JSONArray(productsJson))

        val signature = signContent(message.toString(),username)

        val result = JSONObject()
        result.put("purchase",message)
        result.put("signature",signature)
        Log.d("json", result.toString())
        return result.toString()
    }
}