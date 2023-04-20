package com.example.acme_backend.purchase;

import java.sql.Date;

import com.example.acme_backend.item.AppItem;
import com.example.acme_backend.user.AppUser;
import com.example.acme_backend.voucher.AppVoucher;

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
    private Float total_price;
    private Date date;
    private Boolean emitted;
    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser user;
    @OneToMany(mappedBy = "purchase")
    private Set<AppItem> items = new HashSet<>();
    @OneToOne
    @JoinColumn(name="voucher_id")
    private AppVoucher voucher;

    public AppPurchase() { 
        this.emitted = false;
    }

    public AppPurchase(Long id, Float total_price, Date date) {
        this.id = id;
        this.total_price = total_price;
        this.date = date;
        this.emitted = false;
    }

    public AppPurchase(Float total_price, Date date, AppUser user) {
        this.total_price = total_price;
        this.date = date;
        this.user = user;
        this.emitted = false;
    }

    public Long getId() {
        return id;
    }

    public Float getPrice() {
        return total_price;
    }

    public Date getDate() {
        return date;
    }

    public Boolean getEmitted() {
        return emitted;
    }

    public AppUser getUser() {
        return user;
    }

    public AppVoucher getVoucher() {
        return voucher;
    }

    public Set<AppItem> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrice(Float total_price) {
        this.total_price = total_price;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setEmitted(Boolean emitted) {
        this.emitted = emitted;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public void setItems(Set<AppItem> items) {
        this.items = items;
    }

    public void setVoucher(AppVoucher voucher) {
        this.voucher = voucher;
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

        return "Purchase : {" +
                "id=" + id +
                ", voucher=" + voucher.getUuid() +
                ", price=" + total_price +
                ", date=" + date +
                ", user='" + user.getUsername() + '\'' + 
                ", items=" + itemList +
                '}';
    }

}
