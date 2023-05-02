package com.example.service1.services;

//import com.example.service1.services.GeographicCoverageService;
//import com.example.service1.services.ShippingNotificationService;
import com.example.service1.entities.notifications;


import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@MessageDriven( activationConfig = {
        @jakarta.ejb.ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @jakarta.ejb.ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/ShippingRequestQueue") },
        mappedName = "java:/jms/queue/ShippingRequestQueue", name = "ShippingRequestProcessor")
public class NotificationRequestProcessor implements MessageListener {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    @Override
    public void onMessage(Message message) {
        try
        {
            //PERSIST IN DATABASE
            String orderRequest = message.getBody(String.class);
            System.out.println("Received message: " + orderRequest);
            String[] fruits = orderRequest.split(",");

            LocalDateTime now = LocalDateTime.now();
            String strDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            notifications notification = new notifications(fruits[0],Integer.parseInt(fruits[1]),fruits[2], strDate);

            EntityManager entityManager = null;

            try {
                entityManager = emf.createEntityManager();
                entityManager.getTransaction().begin();
                entityManager.persist(notification);
                entityManager.getTransaction().commit();
            }catch (Exception e) {
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
        catch (JMSException e)
        {
            e.printStackTrace();
        }
    }
}
