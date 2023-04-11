package org.feup.apm.acme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class Vouchers : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.vouchersBackButton)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vouchers)
        backButton.setOnClickListener {
            finish()
        }
    }
}