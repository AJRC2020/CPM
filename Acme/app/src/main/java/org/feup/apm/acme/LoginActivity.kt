package org.feup.apm.acme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class LoginActivity : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.loginBackButton)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        backButton.setOnClickListener {
            finish()
        }
    }
}