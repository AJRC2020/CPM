package org.feup.apm.qrcodeACME

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

import java.security.*
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import kotlin.concurrent.thread

private const val SIZE = 600
private const val ISO_SET = "ISO-8859-1"

class ShowCodeActivity : AppCompatActivity() {
  private var content = ""
  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_show_code)

    val image = findViewById<ImageView>(R.id.img_code)
    val value = intent.getStringExtra("value") ?: ""

    findViewById<TextView>(R.id.tv_title).text =  "QR Code"
    thread {
      encodeAsBitmap(encryptContent(value).toString()).also { runOnUiThread { image.setImageBitmap(it) } }
    }
  }

  private fun getPublic(filename: String): PublicKey? {
    try{
      val file = assets.open("keys/$filename")
      val keyBytes = file.readBytes()
      val spec = X509EncodedKeySpec(keyBytes)
      val kf = KeyFactory.getInstance("RSA")
      return kf.generatePublic(spec)
    }
    catch (e: Exception) {
      Log.d("error", e.message.toString());
      throw e;
    }
  }


  private fun encryptContent(content : String) : String {
    if (content.isEmpty()) throw Exception("No Content")
    return try {
      val prKey = getPublic("publickey.der")
      val result = Cipher.getInstance(Constants.ENC_ALGO).run {
        init(Cipher.ENCRYPT_MODE, prKey)
        doFinal(content.encodeToByteArray())
      }
      android.util.Base64.encodeToString(result, android.util.Base64.NO_WRAP)
    }
    catch (e: Exception) {
      Log.d("error", e.toString())
      throw e
    }
  }



  private fun encodeAsBitmap(str: String): Bitmap? {
    val result: BitMatrix
    val hints = Hashtable<EncodeHintType, String>().apply { put(EncodeHintType.CHARACTER_SET, ISO_SET) }
    val width = SIZE
    try {
      result = MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, width, SIZE, hints)
    }
    catch (e: Exception) {
      content += "\n${e.message}"
      return null
    }
    val w = result.width
    val h = result.height
    val colorDark = resources.getColor(R.color.black, null)
    val colorLight = resources.getColor(R.color.white, null)
    val pixels = IntArray(w * h)
    for (line in 0 until h) {
      val offset = line * w
      for (col in 0 until w)
        pixels[offset + col] = if (result.get(col, line)) colorDark else colorLight
    }
    return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply { setPixels(pixels, 0, w, 0, 0, w, h) }
  }
}
