package com.example.acme_backend.product;

import java.util.HashSet;
import java.util.Set;

import com.example.acme_backend.item.AppItem;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class AppProduct {
    @Id
    @SequenceGenerator(
        name = "product_sequence",
        sequenceName = "product_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "product_sequence"
    )
    @Column(name = "product_id")
    private Long id;
    private String name;
    private Float price;
    private String uuid;
    @OneToMany(mappedBy = "product")
    private Set<AppItem> items = new HashSet<>();

    public AppProduct() { }

    public AppProduct(Long id, String name, Float price, String uuid) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.uuid = uuid;
    }

    public AppProduct(String name, Float price, String uuid) {
        this.name = name;
        this.price = price;
        this.uuid = uuid;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Float getPrice() {
        return price;
    }

    public String getUuid() {
        return uuid;
    }

    public Set<AppItem> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setItems(Set<AppItem> items) {
        this.items = items;
    }

    public void addItem(AppItem item) {
        this.items.add(item);
    }

    public String toString() {
        return "Product : {" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
