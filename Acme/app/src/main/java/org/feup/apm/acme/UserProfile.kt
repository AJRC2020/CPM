package org.feup.apm.acme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class UserProfile : AppCompatActivity() {
    private val backButton by lazy { findViewById<ImageButton>(R.id.profileBackButton)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        backButton.setOnClickListener {
            finish()
        }
    }
}