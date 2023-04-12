package org.feup.apm.acme

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import kotlin.concurrent.thread

class UserProfile : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.profileBackButton)}
    private val navbarReceiptsButton by lazy { findViewById<ImageButton>(R.id.profile_navbar_receipts_button)}
    private val navbarVouchersButton by lazy { findViewById<ImageButton>(R.id.profile_navbar_vouchers_button)}
    private val navbarQRCodeButton by lazy { findViewById<ImageButton>(R.id.profile_navbar_qrcode_button)}
    private val navbarShoppingCartButton by lazy { findViewById<ImageButton>(R.id.profile_navbar_shopping_cart_button)}
    private val nameField by lazy { findViewById<TextView>(R.id.profileNameText)}
    private val usernameField by lazy { findViewById<TextView>(R.id.profileNicknameText)}
    private val disc by lazy {findViewById<TextView>(R.id.profileAccumulatedDiscount)}
    private val tot by lazy {findViewById<TextView>(R.id.profileTotalAmountSpent)}
    private val progressBar by lazy {findViewById<ProgressBar>(R.id.progressBarProfile)}
    private val otherSection by lazy {findViewById<LinearLayout>(R.id.others)}
    private val changePasswordButt by lazy {findViewById<Button>(R.id.profileChangePasswordButton)}
    private val changePaymentMethodButt by lazy {findViewById<Button>(R.id.profileChangePasswordButton)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        val sharedPreference = this.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        nameField.text = sharedPreference.getString("name","error")
        usernameField.text = sharedPreference.getString("username","error")


        loading()
        val uuid = sharedPreference.getString("uuid","none")

        thread{
            uuid?.let {
                getUserInfo(this,
                    it
                )
            }!!

            this.runOnUiThread {
                updateInfo(sharedPreference)
                stopLoading()
            }
        }

        // TODO: change password
        // TODO: change payment method

        navBarList()
    }

    private fun updateInfo(sharedPreference: SharedPreferences){
        disc.text = sharedPreference.getString("discount","error")
        tot.text = sharedPreference.getString("total","error")
    }

    fun createSnackBar(text:String){
        val snack = Snackbar.make(findViewById(android.R.id.content),text, Snackbar.LENGTH_LONG)
        snack.show()
    }


    private fun loading(){
        Log.d("loading","loading")
        progressBar.visibility = View.VISIBLE
        otherSection.visibility = View.GONE
    }

    private fun stopLoading(){
        Log.d("stop","stop")
        progressBar.visibility = View.GONE
        otherSection.visibility = View.VISIBLE
    }

    // TODO: Should be a navbar
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