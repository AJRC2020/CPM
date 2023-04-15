package org.feup.apm.acme.activities

import android.content.Context
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import org.feup.apm.acme.*
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
    private val changePaymentMethodButt by lazy {findViewById<Button>(R.id.profileChangePasswordButton)}
    private val navbar by lazy { findViewById<BottomNavigationView>(R.id.navbar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        backButton.setOnClickListener {
            finish()
        }

        val sharedPreference = this.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        nameField.text = sharedPreference.getString("name","error")
        usernameField.text = sharedPreference.getString("username","error")


        loading(progressBar, listOf(otherSection))
        val uuid = sharedPreference.getString("uuid","none")

        thread{
            uuid?.let {
                getUserInfo(this,
                    it
                )
            }!!

            this.runOnUiThread {
                updateInfo(sharedPreference)
                stopLoading(progressBar,listOf(otherSection))
            }
        }

        // TODO: change password
        // TODO: change payment method
        navBarListeners(navbar,this)
    }

    private fun updateInfo(sharedPreference: SharedPreferences){
        disc.text = sharedPreference.getString("discount","error")
        tot.text = sharedPreference.getString("total","error")
    }

}