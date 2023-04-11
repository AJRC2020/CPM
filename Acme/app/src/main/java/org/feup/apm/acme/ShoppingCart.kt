package org.feup.apm.acme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class ShoppingCart : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.shoppingCartBackButton)}
    private val navbarReceiptsButton by lazy { findViewById<ImageButton>(R.id.shopping_cart_navbar_receipts_button)}
    private val navbarVouchersButton by lazy { findViewById<ImageButton>(R.id.shopping_cart_navbar_vouchers_button)}
    private val navbarQRCodeButton by lazy { findViewById<ImageButton>(R.id.shopping_cart_navbar_qrcode_button)}
    private val navbarProfileButton by lazy { findViewById<ImageButton>(R.id.shopping_cart_navbar_profile_button)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)

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
}