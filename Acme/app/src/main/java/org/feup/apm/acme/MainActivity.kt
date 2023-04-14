package org.feup.apm.acme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val navbar: BottomNavigationView by lazy { findViewById<BottomNavigationView>(R.id.navbar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerButton = findViewById<Button>(R.id.homeRegisterButton)
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java);
            startActivity(intent);
        }


        navbar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navbar_receipts_item -> {
                    val intent1 = Intent(this, Receipts::class.java)
                    startActivity(intent1)
                    true
                }
                R.id.navbar_vouchers_item -> {
                    val intent2 = Intent(this, Vouchers::class.java)
                    startActivity(intent2)
                    true
                }
                R.id.navbar_qrcode_item -> {
                    val intent3 = Intent(this, QRCodeActivity::class.java)
                    startActivity(intent3)
                    true
                }
                R.id.navbar_shopping_cart_item -> {
                    val intent3 = Intent(this, ShoppingCart::class.java)
                    startActivity(intent3)
                    true
                }
                R.id.navbar_profile_item -> {
                    val intent3 = Intent(this, UserProfile::class.java)
                    startActivity(intent3)
                    true
                }
                else -> false
            }
        }
    }
}