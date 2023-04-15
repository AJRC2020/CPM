package org.feup.apm.acme.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.feup.apm.acme.*
import org.feup.apm.acme.fragments.DialogChangePassword
import org.feup.apm.acme.fragments.DialogChangePayment
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
    private var uuid: String = ""

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
        uuid = sharedPreference.getString("uuid","none").toString()

        thread{
            getUserInfo(
                this,
                uuid
            )

            this.runOnUiThread {
                updateInfo(sharedPreference)
                stopLoading(progressBar,listOf(otherSection))
            }
        }

        changePasswordButt.setOnClickListener {
            val popupMenu = DialogChangePassword( uuid,this)
            val manager = supportFragmentManager
            popupMenu.show(manager,"PopUp")
        }

        changePaymentMethodButt.setOnClickListener {
            val popupMenu = DialogChangePayment( uuid,this)
            val manager = supportFragmentManager
            popupMenu.show(manager,"PopUp")
        }

        navBarListeners(navbar,this)
    }

    private fun updateInfo(sharedPreference: SharedPreferences){
        disc.text = convertToEuros(sharedPreference.getFloat("discount",0f))
        tot.text = convertToEuros(sharedPreference.getFloat("total",0f))
    }
}