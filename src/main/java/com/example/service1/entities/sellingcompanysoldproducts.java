package com.example.service1.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "sellingcompanysoldproducts")
public class sellingcompanysoldproducts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name")
    private String customer_name;

    @Column(name = "selling_company_name")
    private String selling_company_name;

    @Column(name = "shipping_company")
    private String shipping_company;

    @Column(name = "product")
    private String product;

    @Column(name = "status")
    private String status;

    @Column(name = "orderId")
    private Long orderId;



    public sellingcompanysoldproducts() {
    }

    public sellingcompanysoldproducts(String customer_name, String selling_company_name, String shipping_company, String product, String status, Long orderId) {
        this.customer_name = customer_name;
        this.selling_company_name = selling_company_name;
        this.shipping_company = shipping_company;
        this.product = product;
        this.status = status;
        this.orderId = orderId;
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getSelling_company_name() {
        return selling_company_name;
    }

    public void setSelling_company_name(String selling_company_name) {
        this.selling_company_name = selling_company_name;
    }

    public String getShipping_company() {
        return shipping_company;
    }

    public void setShipping_company(String shipping_company) {
        this.shipping_company = shipping_company;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}

