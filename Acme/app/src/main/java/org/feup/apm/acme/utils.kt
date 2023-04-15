package org.feup.apm.acme

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import org.feup.apm.acme.activities.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.security.KeyStore
import java.security.Signature

fun navBarListeners(navbar: BottomNavigationView, act: Activity){
    navbar.setOnItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navbar_receipts_item -> {
                val intent1 = Intent(act, Receipts::class.java)
                act.startActivity(intent1)
                true
            }
            R.id.navbar_vouchers_item -> {
                val intent2 = Intent(act, Vouchers::class.java)
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

fun signContent(content:String): String {
    if (content.isEmpty()) throw Exception("no content")

    try {
        val entry = KeyStore.getInstance(Constants.ANDROID_KEYSTORE).run {
            load(null)
            getEntry(Constants.keyName, null)
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