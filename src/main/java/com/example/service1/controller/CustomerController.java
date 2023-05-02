package com.example.service1.controller;

import com.example.service1.entities.*;
import com.example.service1.services.CustomerAccountService;
import com.example.service1.services.GeographicCoverageService;
import com.example.service1.services.ProductSellingCompanyAccountService;
//import com.example.service1.services.PurchaseOrderService;
import com.example.service1.services.PurchaseOrderService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/customer")
@Stateful
public class CustomerController {

    @EJB
    private CustomerAccountService customerAccountService;

    @EJB
    ProductSellingCompanyAccountService productSellingCompanyAccountService;


    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerCustomer(customeraccount customer, @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        try {
            System.out.println("CREATING SHIPPING COMPANY");
            customerAccountService.register(customer.getName(), customer.getUsername(), customer.getPassword(), customer.getLocation(), customer.getEmail());
            //session.setAttribute("username", customer.getUsername());
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to register Customer").build();
        }
    }

    @POST
    @Path("/login/{username}/{password}")
    public Response loginCustomer(@PathParam("username") String username, @PathParam("password") String password, @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (customerAccountService.login(username, password)) {
            session.setAttribute("username", username);
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid username or password").build();
        }
    }

    @POST
    @Path("/addProductToCart/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToCart(@PathParam("name") String name, @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No user loggedIn").build();
        } else {
            TypedQuery<product> query = entityManager.createQuery("SELECT p FROM product p WHERE p.name = :name AND p.quantity > 0", product.class);
            query.setParameter("name", name);

            List<product> resultList = query.getResultList();

            if (!resultList.isEmpty()) {
                // The query returned at least one result
                try {
                    List<String> cart = (List<String>) session.getAttribute("cart");
                    if (cart == null) {
                        cart = new ArrayList<>();
                        session.setAttribute("cart", cart);
                    }
                    cart.add(name);
                    return Response.status(Response.Status.CREATED).build();
                } catch (Exception e) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add product").build();
                }
            } else {
                // The query did not return any results
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Product sold out").build();
            }
        }

    }

    @GET
    @Path("/listCartItems")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listCartItems(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No user loggedIn").build();
        } else {

            try {
                List<String> cart = (List<String>) session.getAttribute("cart");
                if (cart == null) {
                    cart = new ArrayList<>();
                }
                return Response.ok(cart).build();
            } catch (Exception e) {
                return Response.serverError().build();
            }
        }
    }


    @POST
    @Path("/removeProduct/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeProduct(@PathParam("name") String name, @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No user loggedIn").build();
        } else {

            try {
                List<String> cart = (List<String>) session.getAttribute("cart");
                if (cart == null) {
                    cart = new ArrayList<>();
                    session.setAttribute("cart", cart);
                }
                cart.remove(name);
                return Response.status(Response.Status.CREATED).build();
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to remove product").build();
            }
        }
    }

    @GET
    @Path("/clearCart")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCart(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No user loggedIn").build();
        } else {
            try {
                List<String> cart = (List<String>) session.getAttribute("cart");
                if (cart == null) {
                    cart = new ArrayList<>();
                    session.setAttribute("cart", cart);
                }
                cart.clear();
                return Response.status(Response.Status.CREATED).build();
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to clear cart").build();
            }
        }
    }

    @GET
    @Path("/listProducts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<product> listProducts(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return null;
        } else {
            TypedQuery<product> query = entityManager.createQuery("SELECT p FROM product p", product.class);
            return query.getResultList();
        }
    }

    @POST
    @Path("/purchase/{items}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response purchase(@PathParam("items") String items,@Context HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No user loggedIn").build();
        } else {
            Long orderId = PurchaseOrderService.getInstance().createPurchaseOrder(items, username);
            if (orderId == null){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to Purchase Order").build();
            }else{
                customerAccountService.sendToQueue("Order ID[" +orderId+ "]Successfully Added!", username, orderId);
                return Response.status(Response.Status.CREATED).entity("Order Purchased Successfully!").build();
            }
        }
    }


    @GET
    @Path("/viewCurrentOrders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<orders> viewOrders(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return null;
        } else {
            TypedQuery<orders> query = entityManager.createQuery("SELECT o FROM orders o WHERE o.customerName = :name AND o.status IN ('pending', 'processing')", orders.class);
            query.setParameter("name", username);
            List<orders> orders = query.getResultList();
            return orders;
        }
    }

    @GET
    @Path("/viewPastOrders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<orders> viewPastOrders(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return null;
        } else {
            TypedQuery<orders> query = entityManager.createQuery("SELECT o FROM orders o WHERE o.customerName = :name AND o.status = 'shipped'", orders.class);
            query.setParameter("name", username);
            List<orders> orders = query.getResultList();
            return orders;
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

    @GET
    @Path("/viewNotifications")
    @Produces(MediaType.APPLICATION_JSON)
    public List<notifications> viewNotifications(@Context HttpServletRequest request){
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return null;
        } else {
            TypedQuery<notifications> query = entityManager.createQuery("SELECT o FROM notifications o WHERE o.username = :name", notifications.class);
            query.setParameter("name", username);
            List<notifications> notifications = query.getResultList();
            return notifications;
        }
    }



}
