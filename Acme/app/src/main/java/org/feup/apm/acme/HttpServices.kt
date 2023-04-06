package org.feup.apm.acme

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.*

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

// TODO: Estou a usar uma package para as threads melhor perguntar ao stor se há problemas
fun register(act: RegisterActivity, name: String, username: String, password:String, card_number: String, public_key: String) {
  // Creating separate thread to run REST request
  CoroutineScope(Dispatchers.IO).launch {
    // Building URL
    val urlRoute = "api/users/new"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("name", name)
    payload.put("username", username)
    payload.put("password", password)
    payload.put("card_number", card_number)
    payload.put("public_key", public_key)

    var urlConnection: HttpURLConnection? = null
    try {
      // Sending Request
      urlConnection = (withContext(Dispatchers.IO) {
        url.openConnection()
      } as HttpURLConnection).apply {
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
          // Getting response stream
          val read = readStream(inputStream)
          // Parsing stream into JSON
          val jsonObject = JSONObject(read)
          // Saving data into sharedPreferences
          val sharedPreference = act.getSharedPreferences("user_info", Context.MODE_PRIVATE)
          val editor = sharedPreference.edit()
          editor.putString("uuid", jsonObject["uuid"].toString())
          editor.putString("server_public_key", jsonObject["public_key"].toString())
          editor.apply()

          //TODO: CALL ACT.FUNC() TO PROCEED TO NEXT SCREEN

        } else {
          // Putting error info in snack bar
          act.createSnackBar("Code: $responseCode - $errorStream")
          // Putting error info in console
          Log.d("error","Code: $responseCode - $errorStream")
        }
      }
    } catch (e: Exception) {
      // Putting error info in snack bar
      act.createSnackBar(e.toString())
      // Putting error info in console
      Log.d("error",e.toString())
    }finally {
      // Stopping the loading in main thread
      withContext(Dispatchers.Main){
        act.stopLoading()
      }
      // Closing url connection
      urlConnection?.disconnect()
    }
  }
}

