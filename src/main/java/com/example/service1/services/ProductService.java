package com.example.service1.services;

import java.util.List;

import com.example.service1.entities.product;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;



@Stateless
public class ProductService {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    public List<product> listProducts() {
        return entityManager.createQuery("SELECT p FROM product p", product.class).getResultList();
    }

    public void addProduct(String name, int price, String sellerName, int quantity) {
        product product = new product(name, price, sellerName, quantity);

        EntityManager entityManager = null;

        try {
            entityManager = emf.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(product);
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

}
