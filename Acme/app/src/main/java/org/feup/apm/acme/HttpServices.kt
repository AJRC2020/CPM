package org.feup.apm.acme

import android.app.Activity
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
                    disconnect()
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

    signUuid(payload,uuid,username)

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
    newCard: Long,
    uuid: String,
    username: String
) {
    // Building URL
    val urlRoute = "api/users/update/payment"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("uuid", uuid)
    payload.put("payment", newCard)
    signUuid(payload,uuid,username)

    try {
        // Sending Request
        (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            when(responseCode){
                200 -> {
                    disconnect()
                    return
                }
                404 -> {
                    disconnect()
                    throw NotFound("Fatal error, user not found. Please log out.")
                }
                403 -> {
                    disconnect()
                    throw Forbidden("You don't have permission to change the payment method of this user.")
                }
                else -> {
                    disconnect()
                    throw ServerError("Internal server error, please retry.")
                }
            }
        }
    }catch (io: IOException){
        throw ConnectionError("Could not connect to server. Make sure you are connected to the network and retry.")
    }
}


fun changePassword(
    currentPassword: String,
    newPassword: String,
    uuid: String,
    username: String
)  {
    // Building URL
    val urlRoute = "api/users/update/password"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("uuid", uuid)
    payload.put("old_password", currentPassword)
    payload.put("new_password", newPassword)
    signUuid(payload,uuid,username)


    try {
        // Sending Request
        (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            when(responseCode){
                200 -> {
                    disconnect()
                    return
                }
                404 -> {
                    disconnect()
                    throw NotFound("Fatal error, user not found. Please log out.")
                }
                403 -> {
                    disconnect()
                    throw Forbidden("Authentication error. Current password is incorrect, please retry")
                }
                else -> {
                    disconnect()
                    throw ServerError("Internal server error, please retry.")
                }
            }
        }
    }catch (io: IOException){
        throw ConnectionError("Could not connect to server. Make sure you are connected to the network and retry.")
    }
}


fun getPurchases(
    uuid: String,
    username: String
    ) : ArrayList<Receipt> {
    // Building URL
    val urlRoute = "api/users/purchases"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("uuid", uuid)
    signUuid(payload,uuid,username)


    try {
        // Sending Request
        (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            when(responseCode){
                200 -> {
                    disconnect()
                    val read = readStream(inputStream)
                    // Parsing stream into JSON
                    val dataSet = JSONArray(read)
                    disconnect()
                    return createReceiptList(dataSet)
                }
                404 -> {
                    disconnect()
                    throw NotFound("Fatal error, user not found. Please log out.")
                }
                403 -> {
                    disconnect()
                    throw Forbidden("You don't have permission to access this user's receipts")
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
    signUuid(payload,uuid,username)


    try {
        // Sending Request
        (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            when(responseCode){
                200 -> {
                    val read = readStream(inputStream)
                    // Parsing stream into JSON
                    val dataSet = JSONArray(read)
                    disconnect()
                    return createReceiptList(dataSet)
                }
                404 -> {
                    disconnect()
                    throw NotFound("Fatal error, user not found. Please log out.")
                }
                403 -> {
                    disconnect()
                    throw Forbidden("You don't have permission to access this user's receipts")
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


fun getVouchers(
    uuid: String,
    username: String
    ) : VouchersInfo {
    // Building URL
    val urlRoute = "api/users/vouchers"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("uuid", uuid)
    signUuid(payload,uuid,username)

    try {
        // Sending Request
        (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            when(responseCode){
                200 -> {
                    val read = readStream(inputStream)
                    // Parsing stream into JSON
                    val info = JSONObject(read)
                    disconnect()
                    return createVouchersInfo(info)
                }
                404 -> {
                    disconnect()
                    throw NotFound("Fatal error, user not found. Please log out.")
                }
                403 -> {
                    disconnect()
                    throw Forbidden("You don't have permission to access this user's vouchers")
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


fun getProduct(
    encryptedProduct: String,
    ) : Product {
    // Building URL
    val urlRoute = "api/products/new"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("encryption", encryptedProduct)

    try {
        // Sending Request
        (url.openConnection() as HttpURLConnection).apply {
            postRequestSettings(this,payload)
            when(responseCode){
                200 -> {
                    // Getting response stream
                    val read = readStream(inputStream)
                    // Parsing stream into JSON
                    val jsonObject = JSONObject(read)
                    disconnect()
                    return createProduct(jsonObject)
                }
                403 -> {
                    disconnect()
                    throw Forbidden("Invalid QR Code")
                }
                else -> {
                    disconnect()
                    throw ServerError("Internal server error, please retry.")
                }
            }
        }
    }catch (io: IOException){
        throw ConnectionError("Could not connect to server. Make sure you are connected to the network and retry.")
    }
}
