package com.example.acme_backend.user;

import jakarta.persistence.*;

@Entity
@Table
public class AppUser {
    @Id
    @SequenceGenerator(
            name = "student_sequence",
            sequenceName = "student_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_sequence"
    )
    private Long id;
    private String name;
    private String username;
    private String password;
    private String public_key;
    private Long card_number;
    private Long uuid;
    private Float discount;
    private Float total;

    public AppUser() {
    }

    public AppUser(Long id, String name, String username, String password, String public_key, Long card_number, Long uuid, Float discount, Float total) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.public_key = public_key;
        this.card_number = card_number;
        this.uuid = uuid;
        this.discount = discount;
        this.total = total;
    }

    public AppUser(String name, String username, String password, String public_key, Long card_number, Long uuid, Float discount, Float total) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.public_key = public_key;
        this.card_number = card_number;
        this.uuid = uuid;
        this.discount = discount;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPublic_key() {
        return public_key;
    }

    public Long getCard_number() {
        return card_number;
    }

    public Long getUuid() {
        return uuid;
    }

    public Float getDiscount() {
        return discount;
    }

    public Float getTotal() {
        return total;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public void setCard_number(Long card_number) {
        this.card_number = card_number;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", public_key='" + public_key + '\'' +
                ", card_number=" + card_number +
                ", uuid=" + uuid +
                ", discount=" + discount +
                ", total=" + total +
                '}';
    }
}
