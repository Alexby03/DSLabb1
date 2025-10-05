package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.webapp.dslabb1.bo.models.Order;
import se.kth.webapp.dslabb1.bo.models.Worker;
import se.kth.webapp.dslabb1.bo.models.enums.OrderStatus;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.services.OrderService;
import se.kth.webapp.dslabb1.db.data.OrderDAO;
import se.kth.webapp.dslabb1.db.data.UserDAO;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet("/warehouse/orders")
public class WarehouseOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("WAREHOUSEWORKER") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Worker worker = (Worker) session.getAttribute("WAREHOUSEWORKER");
        String orderIdParam = request.getParameter("orderId");
        String filterStatus = request.getParameter("status");

        if (orderIdParam != null && !orderIdParam.trim().isEmpty()) {
            showOrderDetails(request, response, worker, orderIdParam);
        } else {
            showOrderList(request, response, worker, filterStatus);
        }
    }

    private void showOrderList(HttpServletRequest request, HttpServletResponse response,
                               Worker worker, String filterStatus) throws ServletException, IOException {

        List<OrderDAO> allOrders = OrderDAO.findAll();

        List<OrderDAO> filteredOrders = allOrders;
        if (filterStatus != null && !filterStatus.isEmpty() && !"ALL".equals(filterStatus)) {
            try {
                OrderStatus status = OrderStatus.valueOf(filterStatus);
                filteredOrders = allOrders.stream()
                        .filter(o -> o.orderStatus() == status)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {

            }
        }

        request.setAttribute("worker", worker);
        request.setAttribute("orders", filteredOrders);
        request.setAttribute("orderStatuses", OrderStatus.values());
        request.setAttribute("currentFilter", filterStatus != null ? filterStatus : "ALL");

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/warehouse/orders.jsp");
        rd.forward(request, response);
    }

    private void showOrderDetails(HttpServletRequest request, HttpServletResponse response,
                                  Worker worker, String orderIdParam)
            throws ServletException, IOException {

        try {
            UUID orderId = UUID.fromString(orderIdParam);
            Order order = OrderService.getOrderById(orderId, worker.getId(), worker.getUserType());

            if (order == null) {
                request.setAttribute("errorMessage", "Beställning hittades inte.");
                showOrderList(request, response, worker, null);
                return;
            }

            UserDAO customer = UserDAO.findById(order.getCustomerId());
            request.setAttribute("worker", worker);
            request.setAttribute("order", order);
            request.setAttribute("orderItems", order.getItems());
            request.setAttribute("canAdvance",
                    order.getOrderStatus() == OrderStatus.PAID ||
                            order.getOrderStatus() == OrderStatus.SHIPPED);


            if (customer != null) {
                request.setAttribute("customerName", customer.fullName());
                request.setAttribute("customerEmail", customer.email());
                request.setAttribute("customerAddress", customer.address());
            }

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/warehouse/orderDetails.jsp");
            rd.forward(request, response);

        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Ogiltigt beställnings-ID.");
            showOrderList(request, response, worker, null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("WAREHOUSEWORKER") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Worker worker = (Worker) session.getAttribute("WAREHOUSEWORKER");
        String action = request.getParameter("action");
        String orderIdParam = request.getParameter("orderId");

        if ("advanceStatus".equals(action) && orderIdParam != null) {
            advanceOrderStatus(request, worker, orderIdParam);
        }

        response.sendRedirect(request.getContextPath() + "/warehouse/orders?orderId=" + orderIdParam);
    }

    private void advanceOrderStatus(HttpServletRequest request, Worker worker, String orderIdParam) {
        try {
            UUID orderId = UUID.fromString(orderIdParam);
            OrderDAO currentOrder = OrderDAO.findById(orderId);

            OrderStatus nextStatus = null;
            assert currentOrder != null;
            if (currentOrder.orderStatus() == OrderStatus.PAID) {
                nextStatus = OrderStatus.SHIPPED;
            } else if (currentOrder.orderStatus() == OrderStatus.SHIPPED) {
                nextStatus = OrderStatus.DELIVERED;
            }

            if (nextStatus != null) {
                Result result = OrderService.updateOrderStatus(orderId, nextStatus, worker.getUserType());

                if (result == Result.SUCCESS) {
                    request.getSession().setAttribute("successMessage",
                            "Order status: " + nextStatus.name());
                } else {
                    request.getSession().setAttribute("errorMessage", "Kunde inte uppdatera status.");
                }
            } else {
                request.getSession().setAttribute("errorMessage", "Kan inte avancera denna orderstatus.");
            }

        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Fel: " + e.getMessage());
        }
    }
}
