package com.example.service1.services;

import com.example.service1.entities.customeraccount;
import com.example.service1.entities.orders;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.jms.*;
import jakarta.persistence.*;


import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.List;

@Stateless
public class CustomerAccountService {

//    @EJB
//    private GeographicCoverageService coverageService;

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();


    @Resource(mappedName = "java:/jms/queue/ShippingRequestQueue")
    private Queue queue;
    public void sendToQueue(String request2, String username, Long orderId)
    {
        String request = username + "," + orderId + "," + request2;
        try
        {
            Context context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("java:/ConnectionFactory");
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(this.queue);
            ObjectMessage message = session.createObjectMessage();
            message.setObject(request);
            producer.send(message);
            session.close();
            connection.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static boolean isValidInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void register(String name, String username, String password, String location, String email) {
        customeraccount customer = new customeraccount(name, username, password, location, email);

        EntityManager entityManager = null;

        try {
            entityManager = emf.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(customer);
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

    public boolean login(String username, String password) {
        TypedQuery<customeraccount> query = entityManager.createQuery("SELECT c FROM customeraccount c WHERE c.username = :username AND c.password = :password", customeraccount.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        return query.getResultList().size() == 1;
    }

    public List<customeraccount> getAllCustomerAccounts() {
        TypedQuery<customeraccount> query = entityManager.createQuery("SELECT c FROM customeraccount c", customeraccount.class);
        return query.getResultList();
    }
}
