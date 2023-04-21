package com.example.acme_backend.bodies;

public class ReturnProduct {
    public String uuid;
    public String name;
    public Float price;

    public ReturnProduct(String uuid, String name, Float price) {
        this.uuid = uuid;
        this.name = name;
        this.price = price;
    }
}
