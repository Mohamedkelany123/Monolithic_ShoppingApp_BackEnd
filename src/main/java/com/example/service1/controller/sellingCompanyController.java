package com.example.service1.controller;

import com.example.service1.entities.admin;
import com.example.service1.entities.product;
import com.example.service1.entities.productsellingcompany;
import com.example.service1.entities.sellingcompanysoldproducts;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

//AMA BT3ML STATELESS BEYLOGIN MARA WAHDA BAS
@Path("/sellingCompany")
@Stateful
public class sellingCompanyController {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    @POST
    @Path("/login/{company_name}/{password}")
    public Response loginSellingCompany(@PathParam("company_name") String company_name, @PathParam("password") String password, @Context HttpServletRequest request) {
        try {
            productsellingcompany foundSellingCompany = entityManager.createQuery("SELECT a FROM productsellingcompany a WHERE a.company_name = :company_name AND a.password = :password", productsellingcompany.class)
                    .setParameter("company_name", company_name)
                    .setParameter("password", password)
                    .getSingleResult();
            if (foundSellingCompany != null) {
                HttpSession session = request.getSession();
                session.setAttribute("company_name", company_name);
                return Response.status(Response.Status.OK).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid username or password").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid username or password").build();
        } finally {
            entityManager.close();
        }
    }


    @POST
    @Path("/addProduct/{name}/{price}/{quantity}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addProduct(@PathParam("name") String name,@PathParam("price") int price, @PathParam("quantity") int quantity , @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String company_name = (String) session.getAttribute("company_name");
        if (company_name == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No user loggedIn").build();
        } else {
            try {
                System.out.println("product name : " + name);
                System.out.println("price : " + price);
                System.out.println("seller name : " + company_name);
                System.out.println("Quan : " + quantity);
                product product = new product(name, price, company_name, quantity);

                entityManager.getTransaction().begin();
                entityManager.persist(product);
                entityManager.getTransaction().commit();
                return Response.status(Response.Status.CREATED).build();
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add product").build();
            } finally {
                entityManager.close();
            }
        }
    }

    @GET
    @Path("/listProducts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<product> listProducts(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String company_name = (String) session.getAttribute("company_name");
        if (company_name == null) {
            return null;
        } else {
            TypedQuery<product> query = entityManager.createQuery("SELECT p FROM product p WHERE p.sellerName = :company_name", product.class);
            query.setParameter("company_name", company_name);
            return query.getResultList();
        }
    }

    @GET
    @Path("/listSoldProducts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<sellingcompanysoldproducts> listSoldProducts(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String company_name = (String) session.getAttribute("company_name");
        if (company_name == null) {
            return null;
        } else {
            TypedQuery<sellingcompanysoldproducts> query = entityManager.createQuery("SELECT p FROM sellingcompanysoldproducts p WHERE p.selling_company_name = :company_name", sellingcompanysoldproducts.class);
            query.setParameter("company_name", company_name);
            return query.getResultList();
        }
    }

    @POST
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Response.ok().build();
    }




}
