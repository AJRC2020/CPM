package org.feup.apm.acme.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.feup.apm.acme.*
import org.feup.apm.acme.fragments.DialogChangePassword
import org.feup.apm.acme.fragments.DialogChangePayment
import org.feup.apm.acme.fragments.DialogGeneric
import kotlin.concurrent.thread

class UserProfile : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.profileBackButton)}
    private val nameField by lazy { findViewById<TextView>(R.id.profileNameText)}
    private val usernameField by lazy { findViewById<TextView>(R.id.profileNicknameText)}
    private val disc by lazy {findViewById<TextView>(R.id.profileAccumulatedDiscount)}
    private val tot by lazy {findViewById<TextView>(R.id.profileTotalAmountSpent)}
    private val progressBar by lazy {findViewById<ProgressBar>(R.id.progressBarProfile)}
    private val otherSection by lazy {findViewById<LinearLayout>(R.id.others)}
    private val changePasswordButt by lazy {findViewById<Button>(R.id.profileChangePasswordButton)}
    private val changePaymentMethodButt by lazy {findViewById<Button>(R.id.profileChangePaymentButton)}
    private val navbar by lazy { findViewById<BottomNavigationView>(R.id.navbar) }
    private val logOut by lazy { findViewById<Button>(R.id.profileLogoutButton) }
    private var uuid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        checkIfLoggedOut(this)

        val sharedPreference = this.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val username = sharedPreference.getString("username","none").toString()

        getExtraInfo(username,sharedPreference)

        backButton.setOnClickListener {
            finish()
        }

        changePasswordButt.setOnClickListener {
            val popupMenu = DialogChangePassword( uuid,username,this)
            val manager = supportFragmentManager
            popupMenu.show(manager,"PopUp")
        }

        changePaymentMethodButt.setOnClickListener {
            val popupMenu = DialogChangePayment( uuid,username,this)
            val manager = supportFragmentManager
            popupMenu.show(manager,"PopUp")
        }

        logOut.setOnClickListener {
            deleteSharedPreferences("user_info")
            deleteSharedPreferences("shopping_cart_prod_names")
            deleteSharedPreferences("shopping_cart_prod_prices")
            deleteSharedPreferences("shopping_cart_prod_amount")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        navBarListeners(navbar,this)
    }

    private fun getExtraInfo(username: String, sharedPreference: SharedPreferences){

        usernameField.text = username
        uuid = sharedPreference.getString("uuid","none").toString()
        loading(progressBar, listOf(otherSection))
        thread{
            try{
                getUserProfileInfo(
                    this,
                    uuid,username
                )
                this.runOnUiThread {
                    updateInfo(sharedPreference)
                    stopLoading(progressBar,listOf(otherSection))
                }
            }
            catch(e: Exception){
                this.runOnUiThread {
                    stopLoading(progressBar,listOf(otherSection))
                }
                val dialog = e.message?.let { DialogGeneric("Error", it) }
                dialog?.show(supportFragmentManager, "error")
            }
        }
    }

    private fun updateInfo(sharedPreference: SharedPreferences){
        disc.text = convertToEuros(sharedPreference.getFloat("discount",0f))
        tot.text = convertToEuros(sharedPreference.getFloat("total",0f))
        nameField.text = sharedPreference.getString("name","error")
    }
}