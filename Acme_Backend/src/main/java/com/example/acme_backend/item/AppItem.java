package com.example.acme_backend.item;

import com.example.acme_backend.product.AppProduct;
import com.example.acme_backend.purchase.AppPurchase;

import jakarta.persistence.*;

@Entity
@Table(name = "item")
public class AppItem {
    @Id
    @SequenceGenerator(
        name = "item_sequence",
        sequenceName = "item_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "item_sequence"
    )
    @Column(name = "item_id")
    private Long id;
    private Integer quantity;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private AppProduct product;
    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private AppPurchase purchase;

    public AppItem() { }

    public AppItem(Long id, Integer quantity, AppProduct product, AppPurchase purchase) {
        this.id = id;
        this.quantity = quantity;
        this.product = product;
        this.purchase = purchase;
    }

    public AppItem(Integer quantity, AppProduct product, AppPurchase purchase) {
        this.quantity = quantity;
        this.product = product;
        this.purchase = purchase;
    }

    public Long getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public AppProduct getProduct() {
        return product;
    }

    public AppPurchase getPurchase() {
        return purchase;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setProduct(AppProduct product) {
        this.product = product;
    }

    public void setPurchase(AppPurchase purchase) {
        this.purchase = purchase;
    }

    public String toString() {
        return "Item : {" +
                "name='" + product.getName() + '\'' +
                "quantity=" + quantity +
                "total_price=" + (product.getPrice() * quantity) +
                "}";
    }
}
