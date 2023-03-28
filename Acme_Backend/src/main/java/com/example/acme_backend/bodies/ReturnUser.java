package com.example.acme_backend.bodies;

public class ReturnUser {
    public String name;
    public String username;
    public Float discount;
    public Float total;

    public ReturnUser(String name, String username, Float discount, Float total) {
        this.name = name;
        this.username = username;
        this.discount = discount;
        this.total = total;
    }
}
