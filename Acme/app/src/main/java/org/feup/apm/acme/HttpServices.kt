package org.feup.apm.acme

import android.content.Context
import android.util.Log
import org.feup.apm.acme.activities.*
import org.feup.apm.acme.models.Product
import org.feup.apm.acme.models.ProductReceipt
import org.feup.apm.acme.models.Receipt
import org.feup.apm.acme.models.Voucher
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


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
    var uuid : String
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
    return result
}


fun getPurchases(
    act: Receipts,
    uuid: String,

    ) : List<Receipt> {
    // Building URL
    val urlRoute = "api/users/purchases"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("uuid", uuid)
    payload.put("signature", signContent(uuid))


    var urlConnection: HttpURLConnection? = null
    val receipts : MutableList<Receipt> = mutableListOf()
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
                val dataSet = JSONArray(read)


                (0 until dataSet.length()).forEach { receipt ->
                    val date = dataSet.getJSONObject(receipt)["date"].toString()
                    val total = dataSet.getJSONObject(receipt)["price"].toString().toFloat()
                    val voucher = dataSet.getJSONObject(receipt)["voucher"].toString()
                    val itemsJson = dataSet.getJSONObject(receipt).getJSONArray("items")
                    val items : MutableList<ProductReceipt> = mutableListOf()
                    (0 until itemsJson.length()).forEach {
                        val item = itemsJson.getJSONObject(it)

                        val name = item["product"].toString()
                        val price =  item["price"].toString().toFloat()
                        val amount =  item["amount"].toString().toInt()

                        items.add(ProductReceipt(amount,name,price))
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


fun getVouchers(
    act: Vouchers,
    uuid: String,

    ) : List<Voucher> {
    // Building URL
    val urlRoute = "api/users/vouchers"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

    // Creating payload
    val payload = JSONObject()
    payload.put("uuid", uuid)
    payload.put("signature", signContent(uuid))


    var urlConnection: HttpURLConnection? = null
    val vouchers : MutableList<Voucher> = mutableListOf()
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
                val dataSet = JSONArray(read)


                (0 until dataSet.length()).forEach { receipt ->
                    val date = dataSet.getJSONObject(receipt)["date"].toString()
                    val used = dataSet.getJSONObject(receipt)["used"].toString().toBoolean()
                    val emitted = dataSet.getJSONObject(receipt)["emitted"].toString().toBoolean()
                    val id = dataSet.getJSONObject(receipt)["uuid"].toString()
                    vouchers.add(Voucher(emitted,used,date,id))
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
    return vouchers
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
    return result
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
