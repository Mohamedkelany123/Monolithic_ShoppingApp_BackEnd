package com.example.service1.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "orders")
public class orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name")
    private String customerName;

    //status: current status of the order (e.g. "pending", "processing", "shipped")
    @Column(name = "status")
    private String status;

    @Column(name = "products")
    private String products;

    @Column(name = "shipping_company")
    private String shipping_company;

    @Column(name = "totalPrice")
    private int totalPrice;

    @Column(name = "location")
    private String location;

    public orders(){}
    public orders(String customerName, String products, String shipping_company, String status, int totalPrice, String location ) {
        this.customerName = customerName;
        this.products = products;
        this.shipping_company = shipping_company;
        this.status = status;
        this.totalPrice = totalPrice;
        this.location = location;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getStatus() {
        return status;
    }
    public void setStatusProcessing() {
        this.status = "processing";
    }
    public void setStatusShipped() {this.status = "shipped";}
    public String getShipping_company() {return shipping_company;}
    public void setShipping_company(String shipping_company) {this.shipping_company = shipping_company;}
    public int getTotalPrice() {return totalPrice;}
    public void setTotalPrice(int totalPrice) {this.totalPrice = totalPrice;}

    public void setStatus(String status) {this.status = status;}

    public String getLocation() {return location;}

    public void setLocation(String location) {
        this.location = location;
    }
}

