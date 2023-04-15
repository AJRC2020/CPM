package org.feup.apm.acme.models

data class Voucher(
    var emitted: Boolean,
    var used: Boolean,
    var date: String,
    var uuid: String
)
