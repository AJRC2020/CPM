package org.feup.apm.acme

import android.app.Activity
import android.content.Context
import org.json.JSONObject
import java.net.HttpURLConnection


fun saveUserDataRegister(act: Activity, name: String, username: String, jsonObject: JSONObject){
    val sharedPreference = act.getSharedPreferences("user_info", Context.MODE_PRIVATE)
    val editor = sharedPreference.edit()

    val uuid =  jsonObject["uuid"].toString()
    editor.putString("uuid", uuid)
    editor.putString("name", name)
    editor.putString("username", username)
    editor.putString("server_public_key", jsonObject["public_key"].toString())
    editor.apply()
}

fun saveUserDataLogin(act: Activity, uuid: String, username: String){
    val sharedPreference = act.getSharedPreferences("user_info", Context.MODE_PRIVATE)
    val editor = sharedPreference.edit()
    editor.putString("uuid", uuid)
    editor.putString("username", username)
    editor.apply()
}

fun saverExtraUserData(act: Activity, jsonObject: JSONObject){
    val sharedPreference = act.getSharedPreferences("user_info", Context.MODE_PRIVATE)
    val editor = sharedPreference.edit()
    editor.putFloat("discount", jsonObject["discount"].toString().toFloat())
    editor.putFloat("total", jsonObject["total"].toString().toFloat())
    editor.putString("name", jsonObject["name"].toString())
    editor.apply()
}

fun postRequestSettings(urlConnection : HttpURLConnection, payload: JSONObject){
    urlConnection.doOutput = true
    urlConnection.doInput = true
    urlConnection.requestMethod = "POST"
    urlConnection.setRequestProperty("Content-Type", "application/json")
    urlConnection.useCaches = false
    urlConnection.connectTimeout = 5000
    with(urlConnection.outputStream) {
        write(payload.toString().toByteArray())
        flush()
        close()
    }
}

fun getRequestSettings(urlConnection: HttpURLConnection){
    urlConnection.doInput = true
    urlConnection.setRequestProperty("Content-Type", "application/json")
    urlConnection.useCaches = false
    urlConnection.connectTimeout = 5000
}