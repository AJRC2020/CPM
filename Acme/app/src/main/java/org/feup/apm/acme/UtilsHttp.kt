package org.feup.apm.acme

import android.app.Activity
import android.content.Context
import org.feup.apm.acme.models.*
import org.json.JSONArray
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


fun createReceiptList(dataSet: JSONArray) : ArrayList<Receipt>{
    val receipts : ArrayList<Receipt> = arrayListOf()
    (0 until dataSet.length()).forEach { receipt ->
        val date = dataSet.getJSONObject(receipt)["date"].toString()
        val total = dataSet.getJSONObject(receipt)["price"].toString().toFloat()
        val voucher = dataSet.getJSONObject(receipt)["voucher"].toString()
        val itemsJson = dataSet.getJSONObject(receipt).getJSONArray("items")
        val items : ArrayList<ProductAmount> = arrayListOf()
        (0 until itemsJson.length()).forEach {
            val item = itemsJson.getJSONObject(it)

            val uuid = item["uuid"].toString()
            val name = item["product"].toString()
            val price =  item["price"].toString().toFloat()
            val amount =  item["amount"].toString().toInt()

            items.add(ProductAmount(uuid,amount,name,price))
        }
        receipts.add(Receipt(date,total,items,voucher))
    }
    return receipts
}

fun createVouchersInfo(info: JSONObject): VouchersInfo{
    val valueToNext = info["valueToNextVoucher"].toString().toFloat()
    val dataSet = info.getJSONArray("vouchers")
    val vouchers : ArrayList<Voucher> = arrayListOf()

    (0 until dataSet.length()).forEach { receipt ->
        val date = dataSet.getJSONObject(receipt)["date"].toString()
        val used = dataSet.getJSONObject(receipt)["used"].toString().toBoolean()
        val emitted = dataSet.getJSONObject(receipt)["emitted"].toString().toBoolean()
        val id = dataSet.getJSONObject(receipt)["uuid"].toString()
        vouchers.add(Voucher(emitted,used,date,id))
    }

    return VouchersInfo(vouchers,valueToNext)
}

fun createProduct(jsonObject: JSONObject): Product{
    return Product(jsonObject["uuid"].toString(),jsonObject["name"].toString(),jsonObject["price"].toString().toFloat())
}

fun signUuid(payload: JSONObject,uuid:String,username:String){
    try{
        val signature = signContent(uuid,username)
        payload.put("signature", signature)
    }catch(e: SigningException){
        throw e
    }
}