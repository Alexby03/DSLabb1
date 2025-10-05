package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.webapp.dslabb1.bo.models.Admin;
import se.kth.webapp.dslabb1.bo.models.Order;
import se.kth.webapp.dslabb1.bo.models.enums.OrderStatus;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.services.OrderService;
import se.kth.webapp.dslabb1.db.data.OrderDAO;
import se.kth.webapp.dslabb1.db.data.UserDAO;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet("/admin/orders")
public class AdminOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ADMIN") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Admin admin = (Admin) session.getAttribute("ADMIN");
        String orderIdParam = request.getParameter("orderId");

        if (orderIdParam != null && !orderIdParam.trim().isEmpty()) {
            showOrderDetails(request, response, admin, orderIdParam);
        } else {
            showAllOrders(request, response, admin);
        }
    }

    private void showAllOrders(HttpServletRequest request, HttpServletResponse response, Admin admin)
            throws ServletException, IOException {

        List<OrderDAO> allOrders = OrderDAO.findAll();

        request.setAttribute("admin", admin);
        request.setAttribute("orders", allOrders);
        request.setAttribute("orderStatuses", OrderStatus.values());

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp");
        rd.forward(request, response);
    }

    private void showOrderDetails(HttpServletRequest request, HttpServletResponse response,
                                  Admin admin, String orderIdParam)
            throws ServletException, IOException {

        try {
            UUID orderId = UUID.fromString(orderIdParam);
            Order order = OrderService.getOrderById(orderId, admin.getId(), admin.getUserType());

            if (order == null) {
                request.setAttribute("errorMessage", "Beställning hittades inte.");
                showAllOrders(request, response, admin);
                return;
            }

            // Fetch customer information
            UserDAO customer = UserDAO.findById(order.getCustomerId());

            request.setAttribute("admin", admin);
            request.setAttribute("order", order);
            request.setAttribute("orderItems", order.getItems());
            request.setAttribute("orderStatuses", OrderStatus.values());

            if(customer != null) {
                request.setAttribute("customerName", customer.fullName());
                request.setAttribute("customerEmail", customer.email());
                request.setAttribute("customerAddress", customer.address());
            }
            request.setAttribute("customerName", customer.fullName());
            request.setAttribute("customerEmail", customer.email());
            request.setAttribute("customerAddress", customer.address());

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/admin/orderDetails.jsp");
            rd.forward(request, response);

        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Ogiltigt beställnings-ID.");
            showAllOrders(request, response, admin);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ADMIN") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Admin admin = (Admin) session.getAttribute("ADMIN");
        String action = request.getParameter("action");
        String orderIdParam = request.getParameter("orderId");

        if ("updateStatus".equals(action) && orderIdParam != null) {
            updateOrderStatus(request, admin, orderIdParam);
        } else if ("cancelOrder".equals(action) && orderIdParam != null) {
            cancelOrder(request, admin, orderIdParam);
        }

        response.sendRedirect(request.getContextPath() + "/admin/orders?orderId=" + orderIdParam);
    }

    private void updateOrderStatus(HttpServletRequest request, Admin admin, String orderIdParam) {
        try {
            UUID orderId = UUID.fromString(orderIdParam);
            String newStatusStr = request.getParameter("newStatus");
            OrderStatus newStatus = OrderStatus.valueOf(newStatusStr);

            Result result = OrderService.updateOrderStatus(orderId, newStatus, admin.getUserType());

            if (result == Result.SUCCESS) {
                request.getSession().setAttribute("successMessage", "Orderstatus uppdaterad.");
            } else {
                request.getSession().setAttribute("errorMessage", "Kunde inte uppdatera status.");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Fel: " + e.getMessage());
        }
    }

    private void cancelOrder(HttpServletRequest request, Admin admin, String orderIdParam) {
        try {
            UUID orderId = UUID.fromString(orderIdParam);
            Result result = OrderService.cancelOrder(admin.getId(), orderId, admin.getUserType());

            if (result == Result.SUCCESS) {
                request.getSession().setAttribute("successMessage", "Order Cancelled.");
            } else {
                request.getSession().setAttribute("errorMessage", "Kunde inte avbryta ordern.");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Fel: " + e.getMessage());
        }
    }
}
