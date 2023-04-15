package org.feup.apm.acme.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import org.feup.apm.acme.R

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