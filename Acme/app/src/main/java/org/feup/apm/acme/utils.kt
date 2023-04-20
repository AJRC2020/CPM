package org.feup.apm.acme

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.security.KeyPairGeneratorSpec
import android.util.Base64
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import org.feup.apm.acme.Constants.ISO_SET
import org.feup.apm.acme.Constants.SIZE_QR
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

        return Base64.encodeToString(result, Base64.NO_WRAP)
    } catch (e: Exception) {
        throw SigningException("Error while signing a message. Please retry.")
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

fun getPublicKey(username: String) : PublicKey  {
    try {
        val entry = KeyStore.getInstance(Constants.ANDROID_KEYSTORE).run {
            load(null)
            getEntry(username, null)
        }
        return (entry as KeyStore.PrivateKeyEntry).certificate.publicKey
    } catch (ex: Exception) {
        throw KeyException("Could not retrieve public key for user. Make sure you are using the device where your account was created.")
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
        throw GenerateKeysException("Issue with generating client keys. Please try registering again.")
    }
}

fun keysPresent(username: String): Boolean {
    try {
        val entry = KeyStore.getInstance(Constants.ANDROID_KEYSTORE).run {
            load(null)
            getEntry(username, null)
        }
        return (entry != null)
    }
    catch(e: Exception){
        throw KeyException("Error retrieving user keys. Make sure you are using the device you first registered your account in.")
    }

}

fun encodeAsBitmap(str: String, act:Activity): Bitmap? {
    val result: BitMatrix
    val hints = Hashtable<EncodeHintType, String>().apply { put(EncodeHintType.CHARACTER_SET, ISO_SET) }
    val width = SIZE_QR
    try {
        result = MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, width, SIZE_QR, hints)
    }
    catch (e: Exception) {
        throw CreateQRCodeException("There was an issue generating the QR code. Please retry.")
    }
    val w = result.width
    val h = result.height
    val colorDark = act.resources.getColor(R.color.black, null)
    val colorLight = act.resources.getColor(R.color.white, null)
    val pixels = IntArray(w * h)
    for (line in 0 until h) {
        val offset = line * w
        for (col in 0 until w)
            pixels[offset + col] = if (result.get(col, line)) colorDark else colorLight
    }
    return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply { setPixels(pixels, 0, w, 0, 0, w, h) }
}

fun emptyShoppingCart(act: Activity){
    act.deleteSharedPreferences("shopping_cart_prod_names")
    act.deleteSharedPreferences("shopping_cart_prod_prices")
    act.deleteSharedPreferences("shopping_cart_prod_amount")
}
