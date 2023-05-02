package com.example.service1.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price;

    @Column(name = "sellerName")
    private String sellerName;

    @Column(name = "quantity")
    private int quantity;


    public product() {}

    public product(String name, int price, String sellerName, int quantity) {
        this.name = name;
        this.price = price;
        this.sellerName = sellerName;
        this.quantity = quantity;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

//    @Override
//    public String toString() {
//        return "Product [id=" + id + ", name=" + name + ", price=" + price + ", sellerName=" + sellerName + ", quantity=" + quantity + "]";
//    }
}

