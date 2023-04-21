package org.feup.apm.acme.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.feup.apm.acme.*
import org.feup.apm.acme.fragments.DialogGeneric
import java.security.PublicKey
import kotlin.concurrent.thread


class RegisterActivity : AppCompatActivity() {

    private val backButton by lazy { findViewById<ImageButton>(R.id.registerBackButton)}
    private val registerButton by lazy {  findViewById<Button>(R.id.registerConfirmButton) }
    private val nameField by lazy { findViewById<EditText>(R.id.nameField) }
    private val usernameField by lazy { findViewById<EditText>(R.id.registerNicknameFieldInput) }
    private val passwordField by lazy { findViewById<EditText>(R.id.registerPasswordFieldInput)}
    private val paymentMethodField by lazy { findViewById<EditText>(R.id.registerPaymentMethodFieldInput)}
    private val progressBar by lazy {findViewById<ProgressBar>(R.id.progressBar)}
    private val nameError by lazy {findViewById<TextView>(R.id.nameError)}
    private val nicknameError by lazy {findViewById<TextView>(R.id.nicknameError)}
    private val passwordError by lazy {findViewById<TextView>(R.id.passwordError)}
    private val creditCardError by lazy {findViewById<TextView>(R.id.cardError)}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        checkIfLoggedIn(this)


        backButton.setOnClickListener {
            finish()
        }

        registerButton.setOnClickListener {
            register()
        }
    }

    private fun hideAllErrors(){
        nameError.visibility = View.GONE
        nicknameError.visibility = View.GONE
        passwordError.visibility = View.GONE
        creditCardError.visibility = View.GONE
    }

    private fun register(){
        hideAllErrors()

        if (nameField.text.toString().trim().isEmpty()){
            showError(nameError,"The name field needs to be filled.")
            return
        }
        if (usernameField.text.toString().trim().isEmpty()){
            showError(nicknameError,"The nickname field needs to be filled.")
            return
        }
        if (passwordField.text.toString().trim().isEmpty()){
            showError(passwordError,"The password field needs to be filled.")
            return
        }
        if (paymentMethodField.text.toString().trim().isEmpty()){
            showError(creditCardError,"The credit card field needs to be filled.")
            return
        }
        val username = usernameField.text.toString()
        generateAndStoreKeys(username,this)
        val publicKey: PublicKey = getPublicKey(username)
        val encodedPk = publicKey.encoded
        val base64Pk =  android.util.Base64.encodeToString(encodedPk, android.util.Base64.NO_WRAP)
        loading(progressBar, listOf(registerButton))

        thread{
            try{
                register(
                    this,
                    nameField.text.toString(),
                    username,
                    passwordField.text.toString(),
                    paymentMethodField.text.toString(),
                    base64Pk
                )
                this.runOnUiThread {
                    val intent = Intent(this, UserProfile::class.java)
                    startActivity(intent)
                }
            }catch (e : ElementAlreadyInUse){
                this.runOnUiThread {
                    e.message?.let { showError(nicknameError, it) }
                }
            }catch (e : Exception){
                val dialog = e.message?.let { DialogGeneric("Error", it) }
                dialog?.show(supportFragmentManager, "error")
            }
            this.runOnUiThread {
                stopLoading(progressBar, listOf(registerButton))
            }
        }

    }
}

