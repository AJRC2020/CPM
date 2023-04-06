package com.example.acme_backend.bodies;

public class ReturnNewUser {

    public String uuid;
    public byte[] public_key;

    public ReturnNewUser(String uuid, byte[] public_key) {
        this.uuid = uuid;
        this.public_key = public_key;
    }

}
