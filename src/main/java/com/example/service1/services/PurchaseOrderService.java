package com.example.service1.services;

import com.example.service1.entities.orders;
import com.example.service1.entities.product;
import com.example.service1.entities.sellingcompanysoldproducts;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.jms.*;
import jakarta.persistence.*;
import jakarta.ws.rs.core.Response;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Arrays;
import java.util.List;

@Singleton
public class PurchaseOrderService {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();


    private PurchaseOrderService() {}

    private static PurchaseOrderService instance;

    public static synchronized PurchaseOrderService getInstance() {
        if (instance == null) {
            instance = new PurchaseOrderService();
        }
        return instance;
    }

    public Long createPurchaseOrder(String items, String username) throws Exception {
        String[] tokens = items.split(",");
        List<String> cart = Arrays.asList(tokens);

        int success = 0;
        int totalPrice = 0;
        boolean modified = false;
        for (String item : cart) {
            List<product> products = entityManager.createQuery("SELECT p FROM product p WHERE p.name = :item AND p.quantity > 0")
                    .setParameter("item", item)
                    .getResultList();
            if (products.size() > 0) {
                success += 1;
                modified = true;
            }else{
                throw new Exception("Failed To Purchase [" + item + "] is out of stock!");
            }
        }
        if (success != cart.size() || !modified) {
            throw new Exception("None of the updates were successful.");
        }
        for (String item : cart) {
            Query query = entityManager.createQuery("SELECT p.price FROM product p WHERE p.name = :item");
            query.setParameter("item", item);
            Integer price = (Integer) query.getSingleResult();
            totalPrice += price;
        }

        Long orderId = 0L;
        if (cart.size() > 0) {
            String cartStr = String.join(",", cart);
            TypedQuery<String> queryLocation = entityManager.createQuery("SELECT ca.location FROM customeraccount ca WHERE ca.username = :username", String.class);
            queryLocation.setParameter("username", username);
            String location = queryLocation.getSingleResult();
            orders order = new orders(username, cartStr, "-", "pending", totalPrice, location);

            EntityManager entityManager = null;

            try {
                entityManager = emf.createEntityManager();
                entityManager.getTransaction().begin();
                entityManager.persist(order);
                entityManager.getTransaction().commit();
                for (String item : cart) {
                    int rowsUpdated = entityManager.createQuery("UPDATE product p SET p.quantity = p.quantity - 1 WHERE p.name = :item AND p.quantity > 0")
                            .setParameter("item", item)
                            .executeUpdate();
                }
                for (String item : cart) {
                    Query query = entityManager.createQuery("SELECT p.sellerName FROM product p WHERE p.name = :item");
                    query.setParameter("item", item);
                    String selling_company_name = (String) query.getSingleResult();


                    System.out.println("BEFORE QUERY 2");

                    try {
                        TypedQuery<Long> query2 = entityManager.createQuery(
                                "SELECT MAX(p.id) FROM orders p WHERE p.customerName = :name", Long.class);
                        query2.setParameter("name", order.getCustomerName());
                        orderId = query2.getSingleResult();
                    } catch (NoResultException e) {
                        System.out.println("1:" + e);
                    } catch (NonUniqueResultException e) {
                        System.out.println("2 CATCH:" + e);
                    } catch (Exception e) {
                        System.out.println("3:" + e);
                    }

                    if(!GeographicCoverageService.getInstance().isRegionSupported(location)){
//                        sendToQueue("Order Location Not Covered!", username, orderId);
                        Query queryDelete = entityManager.createQuery("DELETE FROM orders o WHERE o.id = :orderId");
                        queryDelete.setParameter("orderId", orderId);
                        queryDelete.executeUpdate();
                        throw new RuntimeException("Order Location Not Covered!");
                    }

                    System.out.println("ORDER ID:" + orderId);
                    sellingcompanysoldproducts sold = new sellingcompanysoldproducts(username, selling_company_name, "-", item, "pending", orderId);
                    System.out.println("usernmae: " + sold.getCustomer_name());
                    System.out.println("sellingCompany: " + sold.getSelling_company_name());
                    System.out.println("shippingCompany: " + sold.getShipping_company());
                    System.out.println("Product: " + sold.getProduct());
                    System.out.println("Status: " + sold.getStatus());
                    System.out.println("BEFORE ADD SOLD PRODUCT");
                    //sellingCompanyService.addSoldProduct(sold);
                    entityManager.getTransaction().begin();
                    entityManager.persist(sold);
                    entityManager.getTransaction().commit();

                    ///////////////////////////////////////////

                }
//                sendToQueue("Order ID[" +orderId+ "]Successfully Added!", username, orderId);
                //////////////////////////////////////////
                //IF THE PURCHASE IS COMMITED THE CART SHOULD BE CLEARED
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
        return orderId;

    }

}
