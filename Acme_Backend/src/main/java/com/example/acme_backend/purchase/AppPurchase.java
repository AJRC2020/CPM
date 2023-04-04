package com.example.acme_backend.purchase;

import java.sql.Date;

import com.example.acme_backend.item.AppItem;
import com.example.acme_backend.user.AppUser;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "purchase")
public class AppPurchase {
    @Id
    @SequenceGenerator(
        name = "purchase_sequence",
        sequenceName = "purchase_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "purchase_sequence"
    )
    @Column(name = "purchase_id")
    private Long id;
    private Boolean voucher;
    private Float total_price;
    private Date date;
    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser user;
    @OneToMany(mappedBy = "purchase")
    private Set<AppItem> items = new HashSet<>();

    public AppPurchase() { }

    public AppPurchase(Long id, Boolean voucher, Float total_price, Date date) {
        this.id = id;
        this.voucher = voucher;
        this.total_price = total_price;
        this.date = date;
    }

    public AppPurchase(Boolean voucher, Float total_price, Date date, AppUser user) {
        this.voucher = voucher;
        this.total_price = total_price;
        this.date = date;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public Boolean getVoucher() {
        return voucher;
    }

    public Float getPrice() {
        return total_price;
    }

    public Date getDate() {
        return date;
    }

    public AppUser getUser() {
        return user;
    }

    public Set<AppItem> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setVoucher(Boolean voucher) {
        this.voucher = voucher;
    }

    public void setPrice(Float total_price) {
        this.total_price = total_price;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public void setItems(Set<AppItem> items) {
        this.items = items;
    }

    public void addItem(AppItem item) {
        this.items.add(item);
    }

    public String toString() {
        Iterator<AppItem> item = items.iterator();
        String itemList = "[";
        while(item.hasNext()) {
            itemList += item.next().toString() + ", "; 
        }
        itemList += "]";

        return "User : {" +
                "id=" + id +
                ", voucher=" + voucher +
                ", price=" + total_price +
                ", date=" + date +
                ", user='" + user.getUsername() + '\'' + 
                ", items=" + itemList +
                '}';
    }

}
