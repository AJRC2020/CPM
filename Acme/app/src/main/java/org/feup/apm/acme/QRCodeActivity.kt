package org.feup.apm.acme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class QRCodeActivity : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.qrcodeBackButton)}
    private val navbarVouchersButton by lazy { findViewById<ImageButton>(R.id.qrcode_navbar_vouchers_button)}
    private val navbarReceiptsButton by lazy { findViewById<ImageButton>(R.id.qrcode_navbar_receipts_button)}
    private val navbarShoppingCartButton by lazy { findViewById<ImageButton>(R.id.qrcode_navbar_shopping_cart_button)}
    private val navbarProfileButton by lazy { findViewById<ImageButton>(R.id.qrcode_navbar_profile_button)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        //Buttons
        backButton.setOnClickListener {
            finish()
        }
        navbarReceiptsButton.setOnClickListener {
            val intent = Intent(this, Receipts::class.java)
            startActivity(intent)
        }
        navbarVouchersButton.setOnClickListener {
            val intent = Intent(this, Vouchers::class.java)
            startActivity(intent)
        }
        navbarShoppingCartButton.setOnClickListener {
            val intent = Intent(this, ShoppingCart::class.java)
            startActivity(intent)
        }
        navbarProfileButton.setOnClickListener {
            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)
        }
    }
}