package com.example.acme_backend.bodies;

public class ProductReceipt {
    public String uuid;
    public String product;
    public Float price;
    public int amount;


    public ProductReceipt(String uuid, String product, Float price, int amount) {
        this.uuid = uuid;
        this.amount = amount;
        this.product = product;
        this.price = price;
    }
}
