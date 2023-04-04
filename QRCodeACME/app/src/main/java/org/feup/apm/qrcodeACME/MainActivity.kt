package org.feup.apm.qrcodeACME

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
  private val price: EditText by lazy { findViewById<EditText>(R.id.priceInput) }
  private val name: EditText by lazy { findViewById<EditText>(R.id.productInput) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    findViewById<Button>(R.id.bt_qr).setOnClickListener { vw -> onButtonClick(vw) }
  }

  private fun onButtonClick(vw: View) {
    val intent = Intent(this, ShowCodeActivity::class.java)
    when (vw.id) {
      R.id.bt_qr -> { intent.putExtra("type", 1)
                      val msg = if (name.text.isEmpty())
                          "You must write the product name"
                      else if (price.text.isEmpty())
                          "You must write the product price"
                      else
                          UUID.randomUUID().toString() +  ":" + name.text.toString() + ":" + price.text.toString()
                      intent.putExtra("value", msg) }
    }
    startActivity(intent)
  }
}