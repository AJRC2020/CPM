package org.feup.apm.acme.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.feup.apm.acme.*
import org.feup.apm.acme.models.ProductAmount
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
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

        val message = buildMessage()

        thread {
            encodeAsBitmap(message,this).also { runOnUiThread { qrCode.setImageBitmap(it) } }
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

    private fun requestPeriodically(){
        val t = Timer()
        t.schedule(object : TimerTask() {
            override fun run() {
                thread {
                    //TODO: check fresh receipt endpoint
                }
            }
        }, 1000)
    }

    private fun buildMessage(): String{

        val sharedPreference = this.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val username = sharedPreference.getString("username","none").toString()
        val uuid = sharedPreference.getString("uuid", "none")

        val message = JSONObject()
        if (voucher != "None"){
            message.put("voucher",voucher)
        }
        message.put("discount",useAcc)
        message.put("user_id",uuid)
        val productsJson : ArrayList<JSONObject> = arrayListOf()
        for (product in products) {
            val prodJ = JSONObject()
            prodJ.put("amount",product.amount)
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