package com.example.acme_backend.user;

import java.util.*;

import com.example.acme_backend.purchase.AppPurchase;
import com.example.acme_backend.voucher.AppVoucher;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
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
    @Column(name = "app_user_id")
    private Long id;
    private String name;
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(unique = true)
    private String public_key;
    @Column(unique = true)
    private Long card_number;
    @Column(unique = true)
    private String uuid;
    private Float discount;
    private Float total;
    @OneToMany(mappedBy = "user")
    private Set<AppPurchase> purchases = new HashSet<>();
    @OneToMany(mappedBy = "user")
    private Set<AppVoucher> vouchers = new HashSet<>();

    public AppUser() {
    }

    public AppUser(Long id, String name, String username, String password, String public_key, Long card_number, String uuid, Float discount, Float total) {
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

    public AppUser(String name, String username, String password, String public_key, Long card_number, String uuid, Float discount, Float total) {
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

    public String getUuid() {
        return uuid;
    }

    public Float getDiscount() {
        return discount;
    }

    public Float getTotal() {
        return total;
    }

    public Set<AppPurchase> getPurchases() {
        return purchases;
    }

    public Set<AppVoucher> getVoucher() {
        return vouchers;
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

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public void setPurchases(Set<AppPurchase> purchases) {
        this.purchases = purchases;
    }

    public void setVouchers(Set<AppVoucher> vouchers) {
        this.vouchers = vouchers;
    }

    public void addDiscount(Float discount) {
        this.discount += discount;
    }

    public void addTotal(Float total) {
        this.total += total;
    }

    public void addVoucher(AppVoucher voucher) {
        this.vouchers.add(voucher);
    }

    public void addPurchase(AppPurchase purchase) {
        this.purchases.add(purchase);
    }

    @Override
    public String toString() {
        return "User : {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", public_key='" + public_key + '\'' +
                ", card_number=" + card_number +
                ", uuid='" + uuid + '\'' +
                ", discount=" + discount +
                ", total=" + total +
                '}';
    }
}
