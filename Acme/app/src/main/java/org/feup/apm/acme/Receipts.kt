package org.feup.apm.acme

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import kotlin.concurrent.thread

class Receipts : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.receiptsBackButton)}
    private val navbarVouchersButton by lazy { findViewById<ImageButton>(R.id.receipts_navbar_vouchers_button)}
    private val navbarQRCodeButton by lazy { findViewById<ImageButton>(R.id.receipts_navbar_qrcode_button)}
    private val navbarShoppingCartButton by lazy { findViewById<ImageButton>(R.id.receipts_navbar_shopping_cart_button)}
    private val navbarProfileButton by lazy { findViewById<ImageButton>(R.id.receipts_navbar_profile_button)}
    private val progressBar by lazy {findViewById<ProgressBar>(R.id.progressBarReceipts)}
    private val layoutReceipts by lazy {findViewById<LinearLayout>(R.id.layoutReceipts)}

    var receipts = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipts)
        navListeners()
        loading()
        val sharedPreference = this.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val uuid = sharedPreference.getString("uuid","none")

        thread{
            receipts = uuid?.let {
                getPurchases(this,
                    it
                )
            }!!

            this.runOnUiThread {
                stopLoading()
                addReceipts()
            }
        }
        stopLoading()
    }


    private fun addReceipts(){
        if (receipts.length() == 0){
            val textView = TextView(this)
            textView.textSize = 30.0F
            textView.text = getString(R.string.no_receipts_warning);
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            layoutReceipts.addView(textView)
        }else{
            (0 until receipts.length()).forEach {
                val receipt = receipts.getJSONObject(it)
                val view = layoutInflater.inflate(R.layout.card_receipt,null)
                layoutReceipts.addView(view)
            }
        }

    }

    fun createSnackBar(text:String){
        val snack = Snackbar.make(findViewById(android.R.id.content),text, Snackbar.LENGTH_LONG)
        snack.show()
    }


    private fun loading(){
        Log.d("loading","loading")
        progressBar.visibility = View.VISIBLE
    }

    private fun stopLoading(){
        Log.d("stop","stop")
        progressBar.visibility = View.GONE

    }


    // TODO: Change to a proper navbar
    private fun navListeners(){
        //Buttons
        backButton.setOnClickListener {
            finish()
        }
        navbarVouchersButton.setOnClickListener {
            val intent = Intent(this, Vouchers::class.java);
            startActivity(intent);
        }
        navbarQRCodeButton.setOnClickListener {
            val intent = Intent(this, QRCodeActivity::class.java);
            startActivity(intent);
        }
        navbarShoppingCartButton.setOnClickListener {
            val intent = Intent(this, ShoppingCart::class.java);
            startActivity(intent);
        }
        navbarProfileButton.setOnClickListener {
            val intent = Intent(this, UserProfile::class.java);
            startActivity(intent);
        }
    }

}