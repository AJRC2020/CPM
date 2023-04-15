package com.example.acme_backend.bodies;

public class ProductReceipt {
    public String product;
    public Float price;
    public int amount;

    public ProductReceipt(String product, Float price, int amount) {
        this.amount = amount;
        this.product = product;
        this.price = price;
    }
}
