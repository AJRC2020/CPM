package org.feup.apm.acme.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import org.feup.apm.acme.*
import org.feup.apm.acme.fragments.DialogGeneric
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.loginBackButton)}
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progressBarLogin)}
    private val loginButton by lazy { findViewById<Button>(R.id.loginConfirmButton)}
    private val loginNicknameFieldInput by lazy { findViewById<EditText>(R.id.loginNicknameFieldInput)}
    private val loginPasswordFieldInput by lazy { findViewById<EditText>(R.id.loginPasswordFieldInput)}
    private val nicknameError by lazy {findViewById<TextView>(R.id.nicknameError)}
    private val passwordError by lazy {findViewById<TextView>(R.id.passwordError)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        checkIfLoggedIn(this)

        loginButton.setOnClickListener {
            login()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun login(){
        hideAllErrors()

        if (loginNicknameFieldInput.text.toString().trim().isEmpty()){
            showError(nicknameError,"The nickname field must be filled.")
            return
        }
        if (loginPasswordFieldInput.text.toString().trim().isEmpty()){
            showError(passwordError,"The password field must be filled.")
            return
        }

        val username = loginNicknameFieldInput.text.toString()
        val password = loginPasswordFieldInput.text.toString()


        if (!keysPresent(username)){
            val dialog = DialogGeneric("Error", "This device is unable to log into this account, please make sure you are using the device you registered your account in.")
            dialog.show(supportFragmentManager, "error")
            return
        }
        
        loading(progressBar, listOf(loginButton))
        thread {
            try {
                login(username, password)
                getUUID(this, username)
                this.runOnUiThread {
                    stopLoading(progressBar, listOf(loginButton))
                    val intent = Intent(this, UserProfile::class.java)
                    startActivity(intent)
                }
            }catch(e: Exception) {
                this.runOnUiThread {
                    stopLoading(progressBar, listOf(loginButton))
                }
                val dialog = e.message?.let { DialogGeneric("Error", it) }
                dialog?.show(supportFragmentManager, "error")
            }
        }

    }


    private fun hideAllErrors(){
        nicknameError.visibility = View.GONE
        passwordError.visibility = View.GONE
    }

}