package org.feup.apm.acme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class ShoppingCart : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.shoppingCartBackButton)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)
        backButton.setOnClickListener {
            finish()
        }
    }
}