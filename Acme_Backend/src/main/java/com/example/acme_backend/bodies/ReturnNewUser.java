package com.example.acme_backend.bodies;

public class ReturnNewUser {

    public String uuid;
    public String public_key;

    public ReturnNewUser(String uuid, String public_key) {
        this.uuid = uuid;
        this.public_key = public_key;
    }

}
