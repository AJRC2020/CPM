package com.example.acme_backend.voucher;

import com.example.acme_backend.purchase.AppPurchase;
import com.example.acme_backend.user.AppUser;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "voucher")
public class AppVoucher {
    @Id
    @SequenceGenerator(
        name = "voucher_sequence",
        sequenceName = "voucher_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "voucher_sequence"
    )
    @Column(name = "voucher_id")
    private Long id;
    private Boolean emitted;
    private Boolean used;
    private Date date;
    @Column(unique = true)
    private String uuid;
    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser user;
    @OneToOne(mappedBy = "voucher")
    private AppPurchase purchase;

    public AppVoucher() { 
        this.emitted = false;
        this.used = false;
    }

    public AppVoucher(String uuid, Date date) {
        this.emitted = false;
        this.used = false;
        this.uuid = uuid;
        this.date = date;
    }

    public AppVoucher(Long id, String uuid, Date date) {
        this.emitted = false;
        this.used = false;
        this.uuid = uuid;
        this.id = id;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public Boolean getUsed() {
        return used;
    }

    public Boolean getEmitted() {
        return emitted;
    }

    public Date getDate() {
        return date;
    }

    public AppUser getUser() {
        return user;
    }

    public AppPurchase getPurchase() {
        return purchase;
    }

    public void setId(Long id) {
        this.id = id;
    }   

    public void setEmitted(Boolean emitted) {
        this.emitted = emitted;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public void setPurchase(AppPurchase purchase) {
        this.purchase = purchase;
    } 

    public String toString() {
        return "Voucher : {" +
                "id=" + id +
                ", uuid=" + uuid +
                ", emitted=" + emitted +
                ", used=" + used +
                ", date=" + date +
                ", user='" + user.getUsername() + '\'' +
                '}';
    }
}
