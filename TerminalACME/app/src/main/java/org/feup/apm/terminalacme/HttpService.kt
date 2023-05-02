package org.feup.apm.terminalacme

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

fun createPurchase(
    message: String,
) {
    val urlRoute = "api/purchases/new"
    val url = URL("https://${Constants.BASE_ADDRESS}${Constants.PORT}/$urlRoute")

    try {
        (url.openConnection() as HttpURLConnection).apply {
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
            when (responseCode){
                200 ->{
                    disconnect()
                    return
                }
                403 -> {
                    disconnect()
                    throw Exception("Authentication error. Please log out and retry.")
                }
                400 -> {
                    disconnect()
                    throw Exception("One or more items in the list are not registered in the system, please rescan the items.")
                }
                404 -> {
                    disconnect()
                    throw Exception("Invalid QR code. Please log out and retry.")
                }
                else -> {
                    disconnect()
                    throw Exception("Internal server error, please retry.")
                }
            }
        }
    } catch (io: IOException){
        throw java.lang.Exception("Could not connect to server. Make sure you are connected to the network and retry.")
    }
}
