package org.feup.apm.acme.models



data class VouchersInfo(
    var vouchers: List<Voucher>,
    var valueToNext: Float
)
