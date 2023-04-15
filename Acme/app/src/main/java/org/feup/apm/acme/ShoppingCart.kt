package org.feup.apm.acme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.feup.apm.acme.models.Product

class ShoppingCart : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.shoppingCartBackButton)}
    private val navbarReceiptsButton by lazy { findViewById<ImageButton>(R.id.shopping_cart_navbar_receipts_button)}
    private val navbarVouchersButton by lazy { findViewById<ImageButton>(R.id.shopping_cart_navbar_vouchers_button)}
    private val navbarQRCodeButton by lazy { findViewById<ImageButton>(R.id.shopping_cart_navbar_qrcode_button)}
    private val navbarProfileButton by lazy { findViewById<ImageButton>(R.id.shopping_cart_navbar_profile_button)}
    private val shoppingList by lazy { findViewById<TextView>(R.id.shoppingList)}
    private val shoppingCartTotal by lazy { findViewById<TextView>(R.id.shoppingCartTotal)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)
        seeProductsInCart()
        //Buttons
        backButton.setOnClickListener {
            finish()
        }
        navbarReceiptsButton.setOnClickListener {
            val intent = Intent(this, Receipts::class.java);
            startActivity(intent);
        }
        navbarVouchersButton.setOnClickListener {
            val intent = Intent(this, Vouchers::class.java);
            startActivity(intent);
        }
        navbarQRCodeButton.setOnClickListener {
            val intent = Intent(this, QRCodeActivity::class.java);
            startActivity(intent);
        }
        navbarProfileButton.setOnClickListener {
            val intent = Intent(this, UserProfile::class.java);
            startActivity(intent);
        }
    }


    private fun deleteProduct(uuid: String){
        val sharedPreference = this.getSharedPreferences("shopping_cart_prod_names", Context.MODE_PRIVATE)
        val sharedPreferencePrices = this.getSharedPreferences("shopping_cart_prod_prices", Context.MODE_PRIVATE)
        val sharedPreferenceAmount = this.getSharedPreferences("shopping_cart_prod_amount", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        val editorPrices = sharedPreferencePrices.edit()
        val editorAmount = sharedPreferenceAmount.edit()
        editor.remove(uuid)
        editor.apply()
        editorPrices.remove(uuid)
        editorPrices.apply()
        editorAmount.remove(uuid)
        editorAmount.apply()
    }

    private fun decreaseProductAmount(uuid: String){

        val sharedPreferenceAmount = this.getSharedPreferences("shopping_cart_prod_amount", Context.MODE_PRIVATE)
        val amount = sharedPreferenceAmount.getInt(uuid,0)

        if (amount <=1){
            deleteProduct(uuid)
        }else{
            val editorAmount = sharedPreferenceAmount.edit()
            editorAmount.putInt(uuid,amount+1)
            editorAmount.apply()
        }
    }


    private fun seeProductsInCart(){

        val sharedPreference = this.getSharedPreferences("shopping_cart_prod_names", Context.MODE_PRIVATE)
        val sharedPreferencePrices = this.getSharedPreferences("shopping_cart_prod_prices", Context.MODE_PRIVATE)
        val sharedPreferenceAmount = this.getSharedPreferences("shopping_cart_prod_amount", Context.MODE_PRIVATE)

        val allEntries: Map<String, *> = sharedPreference.all
        for ((key, value) in allEntries) {
            var finalString = ""
            var total = 0f
            val price = sharedPreferencePrices.getFloat(key,0f)
            val amount = sharedPreferenceAmount.getInt(key,0)
            total += price * amount

            var product = Product(key,value.toString(),price).toString()

            // TODO: Code for testing, Needs to be a recycler adapter later receiving a Array<Product> later
            finalString += amount.toString() + "X - " + Product(key,value.toString(),price).toString()
            val textView = TextView(this)
            textView.text = finalString
            shoppingCartTotal.text = total.toString()

        }


    }


}