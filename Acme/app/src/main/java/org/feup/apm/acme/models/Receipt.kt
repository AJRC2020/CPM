package org.feup.apm.acme.models

data class Receipt(
    var date: String,
    var price: Float,
    var items: MutableList<ProductReceipt>,
    var voucher: String

)
