package org.feup.apm.acme

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView

class UserProfile : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.profileBackButton)}
    private val navbarReceiptsButton by lazy { findViewById<ImageButton>(R.id.profile_navbar_receipts_button)}
    private val navbarVouchersButton by lazy { findViewById<ImageButton>(R.id.profile_navbar_vouchers_button)}
    private val navbarQRCodeButton by lazy { findViewById<ImageButton>(R.id.profile_navbar_qrcode_button)}
    private val navbarShoppingCartButton by lazy { findViewById<ImageButton>(R.id.profile_navbar_shopping_cart_button)}
    private val nameField by lazy { findViewById<TextView>(R.id.profileNameText)}
    private val usernameField by lazy { findViewById<TextView>(R.id.profileNicknameText)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        val sharedPreference = this.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        nameField.text = sharedPreference.getString("name","John Doe")
        usernameField.text = sharedPreference.getString("username","johndoe21")

        // TODO: get accumulated discount
        // TODO: get total amount spent
        // TODO: change password
        // TODO: change payment method

        navBarList()
    }


    //TODO: make proper navbar
    private fun navBarList(){
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
        navbarShoppingCartButton.setOnClickListener {
            val intent = Intent(this, ShoppingCart::class.java);
            startActivity(intent);
        }
    }
}