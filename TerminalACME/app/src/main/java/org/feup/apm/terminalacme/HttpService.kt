package org.feup.apm.terminalacme

import java.net.HttpURLConnection
import java.net.URL

fun createPurchase(
    message: String,
) {
    val urlRoute = "api/purchases/new"
    val url = URL("http://${Constants.BASE_ADDRESS}:${Constants.PORT}/$urlRoute")

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
                write(message.toByteArray())
                flush()
                close()
            }
            if (responseCode != 200) {
                throw Exception("$responseCode  -  $errorStream")
            }
        }
    } catch (e: Exception) {
        throw e
    } finally {
        urlConnection?.disconnect()
    }
}
