package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import se.kth.webapp.dslabb1.bo.models.Customer;
import se.kth.webapp.dslabb1.bo.models.Order;
import se.kth.webapp.dslabb1.bo.models.enums.OrderStatus;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.services.OrderService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "orderServlet", value = "/orders")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/logout");
            return;
        }

        Customer customer = (Customer) session.getAttribute("CUSTOMER");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/logout");
            return;
        }

        String orderIdParam = request.getParameter("orderId");

        try {
            if (orderIdParam != null && !orderIdParam.trim().isEmpty()) {
                // VIEW SPECIFIC ORDER DETAILS
                showOrderDetails(request, response, customer, orderIdParam);
            } else {
                // SHOW ORDER LIST
                showOrderList(request, response, customer);
            }
        } catch (Exception e) {
            System.err.println("Error in OrderServlet: " + e.getMessage());
            request.setAttribute("errorMessage", "Ett fel uppstod när beställningar skulle laddas.");
            showOrderList(request, response, customer);
        }
    }

    private void showOrderList(HttpServletRequest request, HttpServletResponse response, Customer customer)
            throws ServletException, IOException {

        List<Order> orders = OrderService.getCustomerOrders(customer.getId(), customer.getUserType());

        request.setAttribute("orders", orders);
        request.setAttribute("hasOrder", !orders.isEmpty());
        request.setAttribute("orderCount", orders.size());
        request.setAttribute("customerName", customer.getFullName());

        request.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(request, response);
    }

    private void showOrderDetails(HttpServletRequest request, HttpServletResponse response, Customer customer, String orderIdParam)
            throws ServletException, IOException {

        try {
            UUID orderId = UUID.fromString(orderIdParam);
            Order order = OrderService.getOrderById(orderId, customer.getId(), customer.getUserType());

            if (order == null) {
                request.setAttribute("errorMessage", "Beställning hittades inte.");
                showOrderList(request, response, customer);
                return;
            }

            // Verify order belongs to customer
            if (!order.getCustomerId().equals(customer.getId())) {
                request.setAttribute("errorMessage", "Du har inte behörighet att visa denna beställning.");
                showOrderList(request, response, customer);
                return;
            }

            // Set attributes for order details page
            request.setAttribute("order", order);
            request.setAttribute("orderItems", order.getItems());
            request.setAttribute("orderTotal", order.getTotalAmount());
            request.setAttribute("canCancel", order.getOrderStatus() == OrderStatus.PAID);
            //request.setAttribute("dateOfPurchase", order.getDateOfPurchase());

            // Customer info
            request.setAttribute("email", customer.getEmail());
            request.setAttribute("name", customer.getFullName());
            request.setAttribute("address", customer.getAddress());

            request.getRequestDispatcher("/WEB-INF/views/orderDetails.jsp").forward(request, response);

        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Ogiltigt beställnings-ID.");
            showOrderList(request, response, customer);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/logout");
            return;
        }

        Customer customer = (Customer) session.getAttribute("CUSTOMER");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/logout");
            return;
        }

        String action = request.getParameter("action");
        String orderIdParam = request.getParameter("orderId");

        if ("cancelOrder".equals(action) && orderIdParam != null) {
            cancelOrder(request, response, customer, orderIdParam);
        } else {
            doGet(request, response);
        }
    }

    private void cancelOrder(HttpServletRequest request, HttpServletResponse response, Customer customer, String orderIdParam)
            throws ServletException, IOException {

        try {
            UUID orderId = UUID.fromString(orderIdParam);

            Result cancelResult = OrderService.cancelOrder(customer.getId(), orderId, customer.getUserType());

            if (cancelResult == Result.SUCCESS) {
                request.setAttribute("successMessage", "Ordern har avbeställts.");
            } else {
                request.setAttribute("errorMessage", "Kunde inte avbeställa ordern.");
            }

            // Show order details again with message
            showOrderDetails(request, response, customer, orderIdParam);

        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Ogiltigt beställnings-ID.");
            showOrderList(request, response, customer);
        }
    }
}


