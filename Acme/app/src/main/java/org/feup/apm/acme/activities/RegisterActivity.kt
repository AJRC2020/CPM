package org.feup.apm.acme.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.security.KeyPairGeneratorSpec
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.feup.apm.acme.*
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
    private val nameField by lazy { findViewById<EditText>(R.id.newPasswordField) }
    private val usernameField by lazy { findViewById<EditText>(R.id.registerNicknameFieldInput) }
    private val passwordField by lazy { findViewById<EditText>(R.id.registerPasswordFieldInput)}
    private val paymentMethodField by lazy { findViewById<EditText>(R.id.registerPaymentMethodFieldInput)}
    private val progressBar by lazy {findViewById<ProgressBar>(R.id.progressBar)}
    @RequiresApi(Build.VERSION_CODES.O)
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

    private fun register(){
        try{
            if (nameField.text.toString().trim().isNotEmpty() &&
                usernameField.text.toString().trim().isNotEmpty() &&
                passwordField.text.toString().trim().isNotEmpty() &&
                paymentMethodField.text.toString().trim().isNotEmpty()){
                val username = usernameField.text.toString()
                generateAndStoreKeys(username)
                val publicKey: PublicKey = getPublicKey(username)
                val encodedPk = publicKey.encoded
                val base64Pk =  android.util.Base64.encodeToString(encodedPk, android.util.Base64.NO_WRAP)
                loading(progressBar, listOf(registerButton))
                thread{
                    val result = register(
                        this,
                        nameField.text.toString(),
                        username,
                        passwordField.text.toString(),
                        paymentMethodField.text.toString(),
                        base64Pk
                    )
                    this.runOnUiThread {
                        stopLoading(progressBar, listOf(registerButton))
                        if (result){
                            val intent = Intent(this, UserProfile::class.java)
                            startActivity(intent)
                        }
                    }

                }

            }
        }
        catch (ex: Exception){
            createSnackBar(ex.toString(),this)
        }
    }

    private fun getPublicKey(username: String): PublicKey{
        try {
            val entry = KeyStore.getInstance(Constants.ANDROID_KEYSTORE).run {
                load(null)
                getEntry(username, null)
            }
            return (entry as KeyStore.PrivateKeyEntry).certificate.publicKey
        } catch (ex: Exception) {
            throw ex
        }
    }

    private fun generateAndStoreKeys(username: String) {
        try {
            val spec = KeyPairGeneratorSpec.Builder(this)
                .setKeySize(Constants.KEY_SIZE)
                .setAlias(username)
                .setSubject(X500Principal("CN=$username"))
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

