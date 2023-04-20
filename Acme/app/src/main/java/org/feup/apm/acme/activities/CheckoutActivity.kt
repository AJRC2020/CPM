package org.feup.apm.acme.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.feup.apm.acme.*
import org.feup.apm.acme.models.ProductAmount
import org.json.JSONArray
import org.json.JSONObject
import kotlin.concurrent.thread

class CheckoutActivity : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.checkOuttBackButton)}
    private val navbar by lazy { findViewById<BottomNavigationView>(R.id.navbar) }
    private val qrCode by lazy { findViewById<ImageView>(R.id.qrCodePos) }
    private var products = arrayListOf<ProductAmount>()
    private var useAcc = false
    private var voucher = "None"

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
        useAcc = intent.getBooleanExtra("useAcc",false)
        voucher = intent.getStringExtra("voucher") ?: "None"
    }

    private fun requestPeriodically(uuid:String,username: String){
        val handler = Handler(Looper.getMainLooper())
        val delay = 1000
        var stop = false

        handler.postDelayed(object : Runnable {
            override fun run() {
                thread {
                    val receipts = getJustEmittedPurchases(uuid,username)
                    Log.d("receipts", receipts.toString())
                    runOnUiThread {
                        receipts.forEach{
                            if (it.items == products){
                                endPurchase()
                                stop = true
                                return@forEach
                            }
                        }
                        if (!stop) {
                            handler.postDelayed(this,1000)
                        }
                    }
                }
            }
        }, delay.toLong())
    }

    private fun endPurchase(){
        emptyShoppingCart(this)
        val intent = Intent(this, ShoppingCart::class.java)
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