package org.feup.apm.acme.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.feup.apm.acme.R
import org.feup.apm.acme.checkIfLoggedOut
import java.util.*

class PurchaseResponseActivity : AppCompatActivity() {
    private val response by lazy { findViewById<TextView>(R.id.responseText) }
    private var message = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.purchase_result)

        checkIfLoggedOut(this)
        getIntentInfo()

        response.text = message
        timeOut()
    }


    private fun getIntentInfo(){
        val intent = intent
        message = intent.getStringExtra("message") ?: ""
    }

    private fun goToShoppingCart(){
        val intent = Intent(this, ShoppingCart::class.java)
        startActivity(intent)
    }

    private fun timeOut(){
        val t = Timer()
        t.schedule(object : TimerTask() {
            override fun run() {
                goToShoppingCart()
            }
        }, 1000)
    }

}