package com.example.acme_backend.bodies;

import java.sql.Date;
import java.util.List;

public class ReturnPurchase {
    public Date date;
    public Boolean voucher;
    public Float price;
    public List<ProductAndQuantity> items;
    
    public ReturnPurchase(Date date, Boolean voucher, Float price, List<ProductAndQuantity> items) {
        this.date = date;
        this.voucher = voucher;
        this.price = price;
        this.items = items;
    }
}
