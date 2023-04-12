package org.feup.apm.acme

import android.app.Activity
import android.content.Context
import android.provider.ContactsContract.Profile
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyStore
import java.security.Signature

private fun readStream(input: InputStream): String {
    var reader: BufferedReader? = null
    var line: String?
    val response = StringBuilder()
    try {
        reader = BufferedReader(InputStreamReader(input))
        while (reader.readLine().also { line = it } != null)
            response.append(line)
    } catch (e: IOException) {
        response.clear()
        response.append("readStream: ${e.message}")
    }
    reader?.close()
    return response.toString()
}

fun register(
    act: RegisterActivity,
    name: String,
    username: String,
    password: String,
    card_number: String,
    public_key: String
) : Boolean {
    // Building URL
    val urlRoute = "api/users/new"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")
    var uuid = ""
    // Creating payload
    val payload = JSONObject()
    payload.put("name", name)
    payload.put("username", username)
    payload.put("password", password)
    payload.put("card_number", card_number)
    payload.put("public_key", public_key)

    var urlConnection: HttpURLConnection? = null
    var result = false
    try {
        // Sending Request
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
                // Getting response stream
                val read = readStream(inputStream)
                // Parsing stream into JSON
                val jsonObject = JSONObject(read)
                // Saving data into sharedPreferences
                val sharedPreference = act.getSharedPreferences("user_info", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                uuid =  jsonObject["uuid"].toString()
                editor.putString("uuid", uuid)
                editor.putString("name", name)
                editor.putString("username", username)
                editor.putString("server_public_key", jsonObject["public_key"].toString())
                editor.apply()

                result = true
            } else {
                // Putting error info in snack bar
                act.createSnackBar("Code: $responseCode - $errorStream")
                // Putting error info in console
                Log.d("error", "Code: $responseCode - $errorStream")
            }
        }
    } catch (e: Exception) {
        // Putting error info in snack bar
        act.createSnackBar(e.toString())
        // Putting error info in console
        Log.d("error", e.toString())

    } finally {
        // Closing url connection
        urlConnection?.disconnect()
    }
    return result
}


fun getPurchases(
    act: Receipts,
    uuid: String,

) : JSONArray {
    // Building URL
    val urlRoute = "api/users/purchases"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("uuid", uuid)
    payload.put("signature", signContent(uuid))


    var urlConnection: HttpURLConnection? = null
    var result = false
    try {
        // Sending Request
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
                // Getting response stream
                val read = readStream(inputStream)
                // Parsing stream into JSON

                return JSONArray(read);
            } else {
                // Putting error info in snack bar
                //act.createSnackBar("Code: $responseCode - $errorStream")
                // Putting error info in console
                Log.d("error", "Code: $responseCode - $errorStream")
            }
        }
    } catch (e: Exception) {
        // Putting error info in snack bar
        act.createSnackBar(e.toString())
        // Putting error info in console
        Log.d("error", e.toString())

    } finally {
        // Closing url connection
        urlConnection?.disconnect()
    }
    return JSONArray()
}

private fun signContent(content:String): String {
    if (content.isEmpty()) throw Exception("no content")

    try {
        val entry = KeyStore.getInstance(Constants.ANDROID_KEYSTORE).run {
            load(null)
            getEntry(Constants.keyname, null)
        }
        val prKey = (entry as KeyStore.PrivateKeyEntry).privateKey
        val result = Signature.getInstance(Constants.SIGN_ALGO).run {
            initSign(prKey)
            update(content.toByteArray())
            sign()
        }

        val encodedRed = android.util.Base64.encodeToString(result, android.util.Base64.NO_WRAP)
        Log.d("signed content", encodedRed)
        return encodedRed
    }
    catch  (e: Exception) {
        Log.d("error",e.toString())
        throw e
    }
}

fun getUserInfo(
    act: UserProfile,
    uuid: String,

    ) : Boolean {
    // Building URL
    val urlRoute = "api/users/info"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("uuid", uuid)
    payload.put("signature", signContent(uuid))


    var urlConnection: HttpURLConnection? = null
    var result = false
    try {
        // Sending Request
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
                // Getting response stream
                val read = readStream(inputStream)
                // Parsing stream into JSON
                val jsonObject = JSONObject(read)
                val sharedPreference = act.getSharedPreferences("user_info", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putString("discount", jsonObject["discount"].toString())
                editor.putString("total", jsonObject["total"].toString())
                editor.apply()

                result = true
            } else {
                // Putting error info in snack bar
                //act.createSnackBar("Code: $responseCode - $errorStream")
                // Putting error info in console
                Log.d("error", "Code: $responseCode - $errorStream")
            }
        }
    } catch (e: Exception) {
        // Putting error info in snack bar
        act.createSnackBar(e.toString())
        // Putting error info in console
        Log.d("error", e.toString())

    } finally {
        // Closing url connection
        urlConnection?.disconnect()
    }
    return result
}
