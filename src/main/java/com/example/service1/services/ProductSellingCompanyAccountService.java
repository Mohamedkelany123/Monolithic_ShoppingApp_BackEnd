package com.example.service1.services;

import com.example.service1.entities.productsellingcompany;
import com.example.service1.entities.sellingcompanysoldproducts;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Stateless
public class ProductSellingCompanyAccountService {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    public void registerProductSellingCompany(String companyName) {
        String password = generateRandomPassword();
        productsellingcompany company = new productsellingcompany(companyName, password);

        EntityManager entityManager = null;

        try {
            entityManager = emf.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(company);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public List<productsellingcompany> getAllProductSellingCompanies() {
        TypedQuery<productsellingcompany> query = entityManager.createQuery("SELECT c FROM productsellingcompany c", productsellingcompany.class);
        return query.getResultList();
    }


    private String generateRandomPassword() {
        String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
        int PASSWORD_LENGTH = 8;

        Random random = new SecureRandom();

        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();

    }

    public void addSoldProduct(sellingcompanysoldproducts soldProduct) {
        entityManager.getTransaction().begin();
        entityManager.persist(soldProduct);
        entityManager.getTransaction().commit();
    }

}
