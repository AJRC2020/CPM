package org.feup.apm.acme

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import org.feup.apm.acme.Constants.REQUEST_CAMERA_ACCESS
import org.feup.apm.acme.models.Product
import kotlin.concurrent.thread

class QRCodeActivity : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.qrcodeBackButton)}
    private val navbarVouchersButton by lazy { findViewById<ImageButton>(R.id.qrcode_navbar_vouchers_button)}
    private val navbarReceiptsButton by lazy { findViewById<ImageButton>(R.id.qrcode_navbar_receipts_button)}
    private val navbarShoppingCartButton by lazy { findViewById<ImageButton>(R.id.qrcode_navbar_shopping_cart_button)}
    private val navbarProfileButton by lazy { findViewById<ImageButton>(R.id.qrcode_navbar_profile_button)}
    private val openScannerButton by lazy {findViewById<ImageButton>(R.id.openScanner)}
    private val progressBar by lazy {findViewById<ProgressBar>(R.id.progressBarQRCode)}
    private val noScanText by lazy {findViewById<TextView>(R.id.noScanText)}
    private val productName by lazy {findViewById<TextView>(R.id.productNameField)}
    private val productPrice by lazy {findViewById<TextView>(R.id.productPriceField)}
    private val addToCartBtt by lazy { findViewById<Button>(R.id.addToCartButton)}
    private val forgetBtt by lazy { findViewById<ImageButton>(R.id.forgetButton)}
    private val scannedText by lazy {findViewById<LinearLayout>(R.id.productScanned)}
    private var product: Product? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)
        forget()

        addToCartBtt.setOnClickListener{
            addToCart()
        }

        forgetBtt.setOnClickListener{
            forget()
        }

        openScannerButton.setOnClickListener{
            scan()
        }

        navbar()
    }

    private fun addToCart(){
        val sharedPreference = this.getSharedPreferences("shopping_cart_prod_names", Context.MODE_PRIVATE)
        val exists = sharedPreference.getString(product?.uuid, "none")

        if (exists == "none") {
            val editor = sharedPreference.edit()
            editor.putString(product?.uuid, product?.name)
            editor.apply()

            val sharedPreferencePrices =
                this.getSharedPreferences("shopping_cart_prod_prices", Context.MODE_PRIVATE)
            val editorPrices = sharedPreferencePrices.edit()
            product?.price?.let { editorPrices.putFloat(product?.uuid, it) }
            editorPrices.apply()

            val sharedPreferenceAmount =
                this.getSharedPreferences("shopping_cart_prod_amount", Context.MODE_PRIVATE)
            val editorAmount = sharedPreferenceAmount.edit()
            editorAmount.putInt(product?.uuid, 1)
            editorAmount.apply()
        }else{
            val sharedPreferenceAmount =
                this.getSharedPreferences("shopping_cart_prod_amount", Context.MODE_PRIVATE)

            val amount = sharedPreferenceAmount.getInt(product?.uuid, 0)

            val editorAmount = sharedPreferenceAmount.edit()
            editorAmount.putInt(product?.uuid, amount+1)
            editorAmount.apply()
        }
        forget()
    }

    private fun forget(){
        noScanText.visibility = View.VISIBLE
        scannedText.visibility = View.GONE
        product = null
    }


    private fun scan(){
        if (!requestCameraPermission()) {
            readQRCode.launch(IntentIntegrator(this).createScanIntent())
        }
    }

    private fun requestCameraPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if (permission == PackageManager.PERMISSION_GRANTED) return false
        val requests = arrayOf(android.Manifest.permission.CAMERA)
        ActivityCompat.requestPermissions(this, requests, REQUEST_CAMERA_ACCESS)
        return true
    }

    private val readQRCode = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val intentResult : IntentResult? = IntentIntegrator.parseActivityResult(it.resultCode, it.data)
        if (intentResult != null) {
            if (intentResult.contents != null) {
                showProduct(intentResult.contents)
            } else {
                createSnackBar("Failed to scan, please retry.")
            }
        }
    }

    private fun showProduct(content: String){
        loading()
        thread {
            product = getProduct(this,content)
            this.runOnUiThread {
                stopLoading()
            }
        }
    }

    private fun updateProductInfo(){
        productName.text = product?.name
        productPrice.text = product?.price.toString()
    }

    fun createSnackBar(text:String){
        val snack = Snackbar.make(findViewById(android.R.id.content),text, Snackbar.LENGTH_LONG)
        snack.show()
    }


    private fun loading(){
        progressBar.visibility = View.VISIBLE
        noScanText.visibility = View.GONE
        scannedText.visibility = View.GONE
    }

    private fun stopLoading(){
        progressBar.visibility = View.GONE
        if (product == null){
            forget()
        }else{
            scannedText.visibility = View.VISIBLE
            updateProductInfo()
        }
    }


    private fun navbar(){
        //Buttons
        backButton.setOnClickListener {
            finish()
        }
        navbarReceiptsButton.setOnClickListener {
            val intent = Intent(this, Receipts::class.java)
            startActivity(intent)
        }
        navbarVouchersButton.setOnClickListener {
            val intent = Intent(this, Vouchers::class.java)
            startActivity(intent)
        }
        navbarShoppingCartButton.setOnClickListener {
            val intent = Intent(this, ShoppingCart::class.java)
            startActivity(intent)
        }
        navbarProfileButton.setOnClickListener {
            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)
        }
    }
}