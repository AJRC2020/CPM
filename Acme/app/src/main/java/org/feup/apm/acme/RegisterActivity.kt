package org.feup.apm.acme

import android.os.Bundle
import android.security.KeyPairGeneratorSpec
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import java.util.*
import javax.security.auth.x500.X500Principal
import kotlin.concurrent.thread


class RegisterActivity : AppCompatActivity() {

    private val backButton by lazy { findViewById<ImageButton>(R.id.registerBackButton)}
    private val registerButton by lazy {  findViewById<Button>(R.id.registerConfirmButton) }
    private val nameField by lazy { findViewById<EditText>(R.id.registerNameFieldInput) }
    private val usernameField by lazy { findViewById<EditText>(R.id.registerNicknameFieldInput) }
    private val passwordField by lazy { findViewById<EditText>(R.id.registerPasswordFieldInput)}
    private val paymentMethodField by lazy { findViewById<EditText>(R.id.registerPaymentMethodFieldInput)}
    private val progressBar by lazy {findViewById<ProgressBar>(R.id.progressBar)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        backButton.setOnClickListener {
            finish()
        }

        registerButton.setOnClickListener {
            try{
                Log.d("click","click")
                if (nameField.text.toString().trim().isNotEmpty() &&
                    usernameField.text.toString().trim().isNotEmpty() &&
                    passwordField.text.toString().trim().isNotEmpty() &&
                    paymentMethodField.text.toString().trim().isNotEmpty()){
                    Log.d("click","filled")
                    generateAndStoreKeys()
                    val publicKey: PublicKey = getPublicKey()
                    Log.d("pubkey",publicKey.toString())
                    loading()
                    thread {
                        register(this,
                            nameField.text.toString() ,
                            usernameField.text.toString(),
                            passwordField.text.toString(),
                            paymentMethodField.text.toString(),
                            publicKey.encoded.toString()

                        )
                    }
                    //TODO: como é que eu paro o louding screen qnd o pedido acaba
                    // n dá dentro pq só a thread principal e q pode alterar a view
                    // n dá fora pq n há maneira de ver qnd a thread acabou
                    // os handlers estão todos deprecated idk
                }
            }
            catch (ex: Exception){
                Log.d("error",ex.toString())
            }
        }
    }


    fun createSnackBar(text:String){
        val snack = Snackbar.make(findViewById(android.R.id.content),text,Snackbar.LENGTH_LONG)
        snack.show()
    }


    private fun loading(){
        Log.d("loading","loading")
        progressBar.visibility = View.VISIBLE
        registerButton.isEnabled = false
        nameField.isEnabled = false
        usernameField.isEnabled = false
        passwordField.isEnabled = false
        paymentMethodField.isEnabled = false

    }

    private fun stopLoading(){
        Log.d("stop","stop")
        progressBar.visibility = View.GONE
        registerButton.isEnabled = true
        nameField.isEnabled = true
        usernameField.isEnabled = true
        passwordField.isEnabled = true
        paymentMethodField.isEnabled = true

    }

    private fun getPublicKey(): PublicKey{
        try {
            val entry = KeyStore.getInstance(Constants.ANDROID_KEYSTORE).run {
                load(null)
                getEntry(Constants.keyname, null)
            }
            return (entry as KeyStore.PrivateKeyEntry).certificate.publicKey
        } catch (ex: Exception) {
            throw ex
        }
    }

    private fun generateAndStoreKeys() {
        try {
            val spec = KeyPairGeneratorSpec.Builder(this)
                .setKeySize(Constants.KEY_SIZE)
                .setAlias(Constants.keyname)
                .setSubject(X500Principal("CN=" + Constants.keyname))
                .setSerialNumber(BigInteger.valueOf(Constants.serialNr))
                .setStartDate(GregorianCalendar().time)
                .setEndDate(GregorianCalendar().apply { add(Calendar.YEAR, 10) }.time)
                .build()

            KeyPairGenerator.getInstance(Constants.KEY_ALGO, Constants.ANDROID_KEYSTORE).run {
                initialize(spec)
                generateKeyPair()
            }
        }
        catch (ex: Exception) {
            throw ex
        }
    }
}

