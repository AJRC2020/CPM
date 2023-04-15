package org.feup.apm.acme

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.security.KeyPairGeneratorSpec
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import org.feup.apm.acme.activities.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import java.security.Signature
import java.text.NumberFormat
import java.util.*
import javax.security.auth.x500.X500Principal

fun navBarListeners(navbar: BottomNavigationView, act: Activity){
    navbar.setOnItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navbar_receipts_item -> {
                val intent1 = Intent(act, Receipts::class.java)
                act.startActivity(intent1)
                true
            }
            R.id.navbar_vouchers_item -> {
                val intent2 = Intent(act, VouchersActivity::class.java)
                act.startActivity(intent2)
                true
            }
            R.id.navbar_qrcode_item -> {
                val intent3 = Intent(act, QRCodeActivity::class.java)
                act.startActivity(intent3)
                true
            }
            R.id.navbar_shopping_cart_item -> {
                val intent3 = Intent(act, ShoppingCart::class.java)
                act.startActivity(intent3)
                true
            }
            R.id.navbar_profile_item -> {
                val intent3 = Intent(act, UserProfile::class.java)
                act.startActivity(intent3)
                true
            }
            else -> false
        }
    }
}


fun loading(progressBar: ProgressBar, otherSection:List<View>){
    progressBar.visibility = View.VISIBLE
    otherSection.forEach {
        it.visibility = View.GONE
    }
}

fun stopLoading(progressBar: ProgressBar, otherSection:List<View>){
    progressBar.visibility = View.GONE
    otherSection.forEach {
        it.visibility = View.VISIBLE
    }
}

fun createSnackBar(text:String, act: Activity){
    val snack = Snackbar.make(act.findViewById(android.R.id.content),text, Snackbar.LENGTH_LONG)
    snack.show()
}

fun signContent(content:String, username:String): String {
    if (content.isEmpty()) throw Exception("no content")


    try {
        val entry = KeyStore.getInstance(Constants.ANDROID_KEYSTORE).run {
            load(null)
            getEntry(username, null)
        }
        val prKey = (entry as KeyStore.PrivateKeyEntry).privateKey
        val result = Signature.getInstance(Constants.SIGN_ALGO).run {
            initSign(prKey)
            update(content.toByteArray())
            sign()
        }

        val encodedRed = android.util.Base64.encodeToString(result, android.util.Base64.NO_WRAP)
        Log.d("signed content", encodedRed)
        return encodedRed
    }
    catch  (e: Exception) {
        Log.d("error",e.toString())
        throw e
    }
}

fun readStream(input: InputStream): String {
    var reader: BufferedReader? = null
    var line: String?
    val response = StringBuilder()
    try {
        reader = BufferedReader(InputStreamReader(input))
        while (reader.readLine().also { line = it } != null)
            response.append(line)
    } catch (e: IOException) {
        response.clear()
        response.append("readStream: ${e.message}")
    }
    reader?.close()
    return response.toString()
}

fun convertToEuros(value: Float) : String{
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 2
    format.currency = Currency.getInstance("EUR")
    return format.format(value)
}

fun checkIfLoggedOut(act: Activity){
    val sharedPreference = act.getSharedPreferences("user_info", Context.MODE_PRIVATE)
    val uuid = sharedPreference.getString("uuid","none").toString()

    if (uuid == "none"){
        val intent = Intent(act, MainActivity::class.java)
        act.startActivity(intent)
    }
}

fun checkIfLoggedIn(act: Activity){
    val sharedPreference = act.getSharedPreferences("user_info", Context.MODE_PRIVATE)
    val uuid = sharedPreference.getString("uuid","none").toString()

    if (uuid != "none"){
        val intent = Intent(act, UserProfile::class.java)
        act.startActivity(intent)
    }
}

fun getPublicKey(username: String): PublicKey {
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


fun generateAndStoreKeys(username: String, act: Activity) {
    try {
        val spec = KeyPairGeneratorSpec.Builder(act)
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

fun keysPresent(username: String): Boolean {
    val entry = KeyStore.getInstance(Constants.ANDROID_KEYSTORE).run {
        load(null)
        getEntry(username, null)
    }
    return (entry != null)
}