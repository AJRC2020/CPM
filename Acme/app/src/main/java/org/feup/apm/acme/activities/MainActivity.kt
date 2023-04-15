package org.feup.apm.acme.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import org.feup.apm.acme.R
import org.feup.apm.acme.checkIfLoggedIn

class MainActivity : AppCompatActivity() {
    private val registerButton by lazy { findViewById<Button>(R.id.homeRegisterButton)}
    private val loginButton by lazy { findViewById<Button>(R.id.homeLoginButton)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkIfLoggedIn(this)

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}