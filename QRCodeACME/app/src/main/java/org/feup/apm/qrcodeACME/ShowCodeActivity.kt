package org.feup.apm.qrcodeACME

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.security.KeyPairGeneratorSpec
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.io.File
import java.math.BigInteger
import java.nio.charset.Charset
import java.nio.file.Files
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal
import kotlin.concurrent.thread

private const val SIZE = 600
private const val ISO_SET = "ISO-8859-1"

class ShowCodeActivity : AppCompatActivity() {
  private var content = ""
  private var generated = generateAndStoreKeys()
  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_show_code)
    generateAndStoreKeys()

    val image = findViewById<ImageView>(R.id.img_code)
    val type = intent.getIntExtra("type", 0)
    val value = intent.getStringExtra("value") ?: ""
    findViewById<TextView>(R.id.tv_title).text =  "QR Code"
    thread {
      encodeAsBitmap(type, encryptContent(value).toString()).also { runOnUiThread { image.setImageBitmap(it) } }
    }
  }



  private fun getPrivate(filename: String): PrivateKey? {
    try{
      val file = assets.open("keys/$filename")
      val keyBytes = file.readBytes()
      val spec = PKCS8EncodedKeySpec(keyBytes)
      val kf = KeyFactory.getInstance("RSA")
      return kf.generatePrivate(spec)
    }
    catch (e: Exception) {
      Log.d("error", e.message.toString());
      throw e;
    }
  }

  private fun encryptContent(content : String) : ByteArray {
    if (content.isEmpty()) return ByteArray(0)
    return try {
      val prKey = getPrivate("privatekey.pem")
      val result = Cipher.getInstance(Constants.ENC_ALGO).run {
        init(Cipher.ENCRYPT_MODE, prKey)
        doFinal(content.encodeToByteArray())
      }
      Log.d("encrypted",result.toString())
      result
    }
    catch (e: Exception) {
      Log.d("error", e.toString())
      ByteArray(0);
    }
  }


  private fun decryptResult(content: ByteArray)  : ByteArray{
    if (content.isEmpty() ) return ByteArray(0);
    try {
      val entry = KeyStore.getInstance(Constants.ANDROID_KEYSTORE).run {
        load(null)
        getEntry(Constants.keyname, null)
      }
      val puKey = (entry as KeyStore.PrivateKeyEntry).certificate.publicKey
      val clear = Cipher.getInstance(Constants.ENC_ALGO).run {
        init(Cipher.DECRYPT_MODE, puKey)
        doFinal(content)
      }
      return clear

    }
    catch (e: Exception) {
      Log.d("error dec",e.toString())
      return ByteArray(0)
    }
  }




  private fun generateAndStoreKeys(): Boolean {
    try {

      if (!generated){
        val spec = KeyPairGeneratorSpec.Builder(this)
          .setKeySize(Constants.KEY_SIZE)
          .setAlias(Constants.keyname)
          .setSubject(X500Principal("CN=" + Constants.keyname))
          .setSerialNumber(BigInteger.valueOf(Constants.serialNr))
          .setStartDate(GregorianCalendar().time)
          .setEndDate(GregorianCalendar().apply { add(Calendar.YEAR, 10) }.time)
          .build()

        KeyPairGenerator.getInstance(Constants.KEY_ALGO, Constants.ANDROID_KEYSTORE).run {
          initialize(spec)
          generateKeyPair()
        }
      }
    }

    catch (ex: Exception) {
      Log.d("error", ex.toString())
      return false
    }
    return true
  }

  private fun encodeAsBitmap(type: Int, str: String): Bitmap? {
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
