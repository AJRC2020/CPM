package org.feup.apm.terminalacme

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import org.feup.apm.terminalacme.Constants.REQUEST_CAMERA_ACCESS
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private val scan by lazy{findViewById<Button>(R.id.scan)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scan.setOnClickListener {
            scanCode()
        }
    }


    private fun scanCode(){
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
        val intent = Intent(this, ResultActivity::class.java)
        thread {
            if (intentResult != null) {
                if (intentResult.contents != null) {
                    try {
                        createPurchase(intentResult.contents)
                        runOnUiThread {
                            intent.putExtra("title", "Purchase was completed successfully")
                            intent.putExtra(
                                "description",
                                "Thank you for shopping with us, we hope to see you next time."
                            )
                            intent.putExtra("error", false)
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            intent.putExtra("title", "An error has occurred")
                            intent.putExtra("description", e.message)
                            intent.putExtra("error", true)
                        }
                    }

                } else {
                    runOnUiThread {
                        intent.putExtra("title", "An error has occurred")
                        intent.putExtra(
                            "description",
                            "There was an error while scanning the QR code, please retry"
                        )
                        intent.putExtra("error", true)
                    }
                }
            } else {
                runOnUiThread {
                    intent.putExtra("title", "An error has occurred")
                    intent.putExtra(
                        "description",
                        "There was an error while scanning the QR code, please retry"
                    )
                    intent.putExtra("error", true)
                }
            }
            runOnUiThread {
                startActivity(intent)
            }
        }
    }
}