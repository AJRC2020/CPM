package org.feup.apm.acme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class Vouchers : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.vouchersBackButton)}

    private val navbarReceiptsButton by lazy { findViewById<ImageButton>(R.id.vouchers_navbar_receipts_button)}
    private val navbarQRCodeButton by lazy { findViewById<ImageButton>(R.id.vouchers_navbar_qrcode_button)}
    private val navbarShoppingCartButton by lazy { findViewById<ImageButton>(R.id.vouchers_navbar_shopping_cart_button)}
    private val navbarProfileButton by lazy { findViewById<ImageButton>(R.id.vouchers_navbar_profile_button)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vouchers)


        //Buttons
        backButton.setOnClickListener {
            finish()
        }
        navbarReceiptsButton.setOnClickListener {
            val intent = Intent(this, Receipts::class.java);
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