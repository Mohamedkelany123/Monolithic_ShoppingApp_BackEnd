package com.example.service1.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "productsellingcompany")
public class productsellingcompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false, unique = true)
    private String company_name;

    @Column(name = "password", nullable = false)
    private String password;


    public productsellingcompany() {}

    public productsellingcompany(String company_name, String password) {
        this.company_name = company_name;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return company_name;
    }

    public void setCompanyName(String companyName) {
        this.company_name = companyName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

