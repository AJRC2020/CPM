package org.feup.apm.acme

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

private fun readStream(input: InputStream): String {
  var reader: BufferedReader? = null
  var line: String?
  val response = StringBuilder()
  try {
    reader = BufferedReader(InputStreamReader(input))
    while (reader.readLine().also{ line = it } != null)
      response.append(line)
  }
  catch (e: IOException) {
    response.clear()
    response.append("readStream: ${e.message}")
  }
  reader?.close()
  return response.toString()
}

//**************************************************************************
// Function to call REST operation GetUsers
fun register(act: RegisterActivity, name: String, username: String, password:String, card_number: String, public_key: String) {
  CoroutineScope(Dispatchers.IO).launch {

    val urlRoute = "api/users/new"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")
    Log.d("url", url.toString())

    val payload = JSONObject()
    payload.put("name", name)
    payload.put("username", username)
    payload.put("password", password)
    payload.put("card_number", card_number)
    payload.put("public_key", public_key)

    Log.d("payload", payload.toString())
    var urlConnection: HttpURLConnection? = null
    try {
      urlConnection = (url.openConnection() as HttpURLConnection).apply {
        doOutput = true
        doInput = true
        requestMethod = "POST"
        setRequestProperty("Content-Type", "application/json")
        useCaches = false
        connectTimeout = 5000
        with(outputStream) {
          write(payload.toString().toByteArray())
          flush()
          close()
        }
        if (responseCode == 200) {

          val read = readStream(inputStream)
          val jsonObject = JSONObject(read)
          Log.d("uuid", jsonObject["uuid"].toString())
          Log.d("public_key", jsonObject["public_key"].toString().toByteArray().toString())

          /*
        val spec = X509EncodedKeySpec(jsonObject["public_key"].toString().toByteArray())
        val kf = KeyFactory.getInstance("RSA")
        val pk =  kf.generatePublic(spec)
         */
          // TODO: key format is incorrect TT above should work

          val sharedPreference = act.getSharedPreferences("user_info", Context.MODE_PRIVATE)
          val editor = sharedPreference.edit()
          editor.putString("uuid", jsonObject["uuid"].toString())
          editor.putString("server_public_key", jsonObject["public_key"].toString())
          editor.apply()

          //TODO: CALL ACT.FUNC() TO PROCEED TO NEXT SCREEN
        } else
          act.createSnackBar("Code: $responseCode - $errorStream")
      }
    } catch (e: Exception) {
      act.createSnackBar(e.toString())
    }finally {
      withContext(Dispatchers.Main){
        act.stopLoading()
      }
      urlConnection?.disconnect()
    }
  }
}

