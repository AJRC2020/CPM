package org.feup.apm.acme

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import org.feup.apm.acme.Constants.REQUEST_CAMERA_ACCESS

class QRCodeActivity : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.qrcodeBackButton)}
    private val navbarVouchersButton by lazy { findViewById<ImageButton>(R.id.qrcode_navbar_vouchers_button)}
    private val navbarReceiptsButton by lazy { findViewById<ImageButton>(R.id.qrcode_navbar_receipts_button)}
    private val navbarShoppingCartButton by lazy { findViewById<ImageButton>(R.id.qrcode_navbar_shopping_cart_button)}
    private val navbarProfileButton by lazy { findViewById<ImageButton>(R.id.qrcode_navbar_profile_button)}
    private val openScannerButton by lazy {findViewById<Button>(R.id.openScanner)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)
        navbar()
        openScannerButton.setOnClickListener{
            scan()
        }

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
                //processQRCode(intentResult)
                Log.d("result", intentResult.toString())
            } else {
                //showToast(this, "Scan failed")
            }
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