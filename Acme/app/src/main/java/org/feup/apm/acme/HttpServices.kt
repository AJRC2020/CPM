package org.feup.apm.acme

import android.app.Activity
import android.content.Context
import android.util.Log
import org.feup.apm.acme.activities.*
import org.feup.apm.acme.models.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


fun register(
    act: Activity,
    name: String,
    username: String,
    password: String,
    card_number: String,
    public_key: String
)  {
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
        urlConnection = (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            when(responseCode){
                200 -> {
                    // Getting response stream
                    val read = readStream(inputStream)

                    // Parsing stream into JSON
                    val jsonObject = JSONObject(read)

                    // Saving data into sharedPreferences
                    saveUserDataRegister(act,name,username,jsonObject)
                }
                226 -> {
                    disconnect()
                    throw ElementAlreadyInUse("Username is already in use")
                }
                500 -> {
                    disconnect()
                    throw ServerError("Internal server error, please retry.")
                }
                else ->  {
                    disconnect()
                    throw ServerError ("Internal server error, please retry.") }
            }
        }
    } catch (io: IOException){
        throw ConnectionError("Could not connect to server. Make sure you are connected to the network and retry.")
    }
    finally {
        // Closing url connection
        urlConnection?.disconnect()
    }
}

fun login(
    username: String,
    password: String,
) {
    // Building URL
    val urlRoute = "api/users/login"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("username", username)
    payload.put("password", password)


    try {
        (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            when (responseCode){
                200 ->{
                    return
                }
                403 -> {
                    disconnect()
                    throw Forbidden("Nickname and Password do not match, please retry.")
                }
                404 -> {
                    disconnect()
                    throw NotFound("User not found, make sure the username is correct.")
                }
                else -> {
                    disconnect()
                    throw ServerError("Internal server error, please retry.")
                }
            }

        }
    } catch (io: IOException){
        throw ConnectionError("Could not connect to server. Make sure you are connected to the network and retry.")
    }
}


fun getUUID(
    act: Activity,
    username: String
)  {
    // Building URL
    val urlRoute = "api/users/uuid/$username"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    var urlConnection: HttpURLConnection? = null
    try {
        // Sending Request
        urlConnection = (url.openConnection() as HttpURLConnection).apply {
            getRequestSettings(this)
            when (responseCode){
                200 -> {
                    val uuid = readStream(inputStream)
                    saveUserDataLogin(act,uuid,username)
                }
                404 -> {
                    disconnect()
                    throw NotFound("User not found, make sure the username is correct.")
                }
                else -> {
                    disconnect()
                    throw ServerError("Internal server error, please retry.")
                }

            }
        }
    } catch (io: IOException){
        throw ConnectionError("Could not connect to server. Make sure you are connected to the network and retry.")
    }finally {
        urlConnection?.disconnect()
    }
}

fun getUserProfileInfo(
    act: Activity,
    uuid: String,
    username: String
)  {
    // Building URL
    val urlRoute = "api/users/info"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("uuid", uuid)

    val signature : String

    try{
        signature = signContent(uuid,username)
    }catch(e: SigningException){
        throw e
    }

    payload.put("signature", signature)


    var urlConnection: HttpURLConnection? = null
    try {
        // Sending Request
        urlConnection = (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            when (responseCode){
                200 -> {
                    // Getting response stream
                    val read = readStream(inputStream)
                    // Parsing stream into JSON
                    val jsonObject = JSONObject(read)
                    saverExtraUserData(act,jsonObject)
                }
                404 -> {
                    disconnect()
                    throw NotFound("Fatal error, user not found. Please log out.")
                }
                403 -> {
                    disconnect()
                    throw Forbidden("You don't have permission to retrieve this user's information.")
                }
                else -> {
                    disconnect()
                    throw ServerError("Internal server error, please retry.")
                }

            }
        }
    }catch (io: IOException){
        throw ConnectionError("Could not connect to server. Make sure you are connected to the network and retry.")
    } finally {
        urlConnection?.disconnect()
    }
}

fun changePaymentMethod(
    act: UserProfile,
    newCard: Long,
    uuid: String,
    username: String
) : Boolean {
    // Building URL
    val urlRoute = "api/users/update/payment"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("uuid", uuid)
    payload.put("payment", newCard)
    payload.put("signature", signContent(uuid,username))


    var urlConnection: HttpURLConnection? = null
    var result = false
    try {
        // Sending Request
        urlConnection = (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            if (responseCode == 200) {
                result = true

            } else {
                // Putting error info in snack bar
                createSnackBar("Code: $responseCode - $errorStream", act)
                // Putting error info in console
                Log.d("error", "Code: $responseCode - $errorStream")
            }
        }
    } catch (e: Exception) {
        // Putting error info in snack bar
        createSnackBar(e.toString(),act)
        // Putting error info in console
        Log.d("error", e.toString())

    } finally {
        // Closing url connection
        urlConnection?.disconnect()
    }
    return result
}


fun changePassword(
    act: UserProfile,
    currentPassword: String,
    newPassword: String,
    uuid: String,
    username: String
) : Boolean {
    // Building URL
    val urlRoute = "api/users/update/password"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("uuid", uuid)
    payload.put("old_password", currentPassword)
    payload.put("new_password", newPassword)
    payload.put("signature", signContent(uuid,username))


    var urlConnection: HttpURLConnection? = null
    var result = false
    try {
        // Sending Request
        urlConnection = (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            if (responseCode == 200) {
                result = true

            } else {
                // Putting error info in snack bar
                createSnackBar("Code: $responseCode - $errorStream", act)
                // Putting error info in console
                Log.d("error", "Code: $responseCode - $errorStream")
            }
        }
    } catch (e: Exception) {
        // Putting error info in snack bar
        createSnackBar(e.toString(),act)
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
    username: String
    ) : ArrayList<Receipt> {
    // Building URL
    val urlRoute = "api/users/purchases"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("uuid", uuid)
    payload.put("signature", signContent(uuid,username))


    var urlConnection: HttpURLConnection? = null
    val receipts : ArrayList<Receipt> = arrayListOf()
    try {
        // Sending Request
        urlConnection = (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            if (responseCode == 200) {
                // Getting response stream
                val read = readStream(inputStream)
                // Parsing stream into JSON
                val dataSet = JSONArray(read)


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

            } else {
                // Putting error info in snack bar
                createSnackBar("Code: $responseCode - $errorStream", act)
                // Putting error info in console
                Log.d("error", "Code: $responseCode - $errorStream")
            }
        }
    } catch (e: Exception) {
        // Putting error info in snack bar
        createSnackBar(e.toString(),act)
        // Putting error info in console
        Log.d("error", e.toString())

    } finally {
        // Closing url connection
        urlConnection?.disconnect()
    }
    return receipts
}

fun getJustEmittedPurchases(
    uuid: String,
    username: String
) : ArrayList<Receipt> {
    // Building URL
    val urlRoute = "api/users/purchases/emitted"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("uuid", uuid)
    payload.put("signature", signContent(uuid,username))


    var urlConnection: HttpURLConnection? = null
    val receipts : ArrayList<Receipt> = arrayListOf()
    try {
        // Sending Request
        urlConnection = (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            if (responseCode == 200) {
                // Getting response stream
                val read = readStream(inputStream)
                // Parsing stream into JSON
                val dataSet = JSONArray(read)


                (0 until dataSet.length()).forEach { receipt ->
                    val date = dataSet.getJSONObject(receipt)["date"].toString()
                    val total = dataSet.getJSONObject(receipt)["price"].toString().toFloat()
                    val voucher = dataSet.getJSONObject(receipt)["voucher"].toString()
                    val itemsJson = dataSet.getJSONObject(receipt).getJSONArray("items")
                    val items : ArrayList<ProductAmount> = arrayListOf()
                    (0 until itemsJson.length()).forEach {
                        val item = itemsJson.getJSONObject(it)

                        val prodUuid = item["uuid"].toString()
                        val name = item["product"].toString()
                        val price =  item["price"].toString().toFloat()
                        val amount =  item["amount"].toString().toInt()

                        items.add(ProductAmount(prodUuid,amount,name,price))
                    }
                    receipts.add(Receipt(date,total,items,voucher))
                }

            } else {
                // Putting error info in console
                Log.d("error", "Code: $responseCode - $errorStream")
            }
        }
    } catch (e: Exception) {

        // Putting error info in console
        Log.d("error", e.toString())

    } finally {
        // Closing url connection
        urlConnection?.disconnect()
    }
    return receipts
}


fun getVouchers(
    act: Activity,
    uuid: String,
    username: String
    ) : VouchersInfo {
    // Building URL
    val urlRoute = "api/users/vouchers"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("uuid", uuid)
    payload.put("signature", signContent(uuid,username))


    var urlConnection: HttpURLConnection? = null

    var vouchersInfo = VouchersInfo(arrayListOf(),0f)
    try {
        // Sending Request
        urlConnection = (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            if (responseCode == 200) {
                // Getting response stream
                val read = readStream(inputStream)
                // Parsing stream into JSON

                val info = JSONObject(read)
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

                vouchersInfo = VouchersInfo(vouchers,valueToNext)

            } else {
                // Putting error info in snack bar
                createSnackBar("Code: $responseCode - $errorStream", act)
                // Putting error info in console
                Log.d("error", "Code: $responseCode - $errorStream")
            }
        }
    } catch (e: Exception) {
        // Putting error info in snack bar
        createSnackBar(e.toString(),act)
        // Putting error info in console
        Log.d("error", e.toString())

    } finally {
        // Closing url connection
        urlConnection?.disconnect()
    }
    return vouchersInfo
}


fun getProduct(
    act: QRCodeActivity,
    encryptedProduct: String,

    ) : Product? {
    // Building URL
    val urlRoute = "api/products/new"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("encryption", encryptedProduct)


    var urlConnection: HttpURLConnection? = null
    try {
        // Sending Request
        urlConnection = (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            if (responseCode == 200) {
                // Getting response stream
                val read = readStream(inputStream)
                // Parsing stream into JSON
                val jsonObject = JSONObject(read)

                return Product(jsonObject["uuid"].toString(),jsonObject["name"].toString(),jsonObject["price"].toString().toFloat())
            } else {
                // Putting error info in snack bar
                createSnackBar("Code: $responseCode - $errorStream",act)
            }
        }
    } catch (e: Exception) {
        // Putting error info in snack bar
        createSnackBar(e.toString(),act)

    } finally {
        // Closing url connection
        urlConnection?.disconnect()
    }

    return null
}
