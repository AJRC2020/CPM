package org.feup.apm.acme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class Receipts : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.receiptsBackButton)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipts)
        backButton.setOnClickListener {
            finish()
        }
    }
}