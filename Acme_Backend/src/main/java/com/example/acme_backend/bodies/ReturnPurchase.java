package com.example.acme_backend.bodies;

import java.sql.Date;
import java.util.List;

public class ReturnPurchase {
    public Date date;
    public Float price;
    public List<ProductReceipt> items;
    public String voucher;
    
    public ReturnPurchase(Date date, Float price, List<ProductReceipt> items, String voucher) {
        this.date = date;
        this.price = price;
        this.items = items;
        this.voucher = voucher;
    }

    public ReturnPurchase(Date date, Float price, List<ProductReceipt> items) {
        this.date = date;
        this.price = price;
        this.items = items;
    }
}
