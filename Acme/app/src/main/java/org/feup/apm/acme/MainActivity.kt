package org.feup.apm.acme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerButton = findViewById<Button>(R.id.homeRegisterButton)
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java);
            startActivity(intent);
        }
    }
}