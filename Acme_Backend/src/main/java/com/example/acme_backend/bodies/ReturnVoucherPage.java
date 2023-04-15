package com.example.acme_backend.bodies;

import java.util.List;

public class ReturnVoucherPage {
    public List<ReturnVoucher> vouchers;
    public Float valueToNextVoucher;

    public ReturnVoucherPage(List<ReturnVoucher> vouchers, Float valueToNextVoucher) {
        this.vouchers = vouchers;
        this.valueToNextVoucher = valueToNextVoucher;
    }
}
