package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.services.CartService;
import se.kth.webapp.dslabb1.bo.services.OrderService;
import se.kth.webapp.dslabb1.ui.info.CartInfo;
import se.kth.webapp.dslabb1.ui.info.CartItemInfo;
import se.kth.webapp.dslabb1.ui.info.UserInfo;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "checkoutServlet", value = "/checkout")
public class CheckoutServlet extends HttpServlet {

    private void loadCheckoutData(HttpServletRequest request, UserInfo customer) {
        CartInfo userCart = CartService.getUserCart(customer.userId(), customer.userType());
        List<CartItemInfo> cartItems = (userCart != null) ? userCart.items() : List.of();
        double cartTotal = CartService.getCartTotal(customer.userId(), customer.userType());
        int cartItemCount = CartService.getCartItemCount(customer.userId(), customer.userType());

        request.setAttribute("email", customer.email());
        request.setAttribute("name", customer.fullName());
        request.setAttribute("address", customer.address());
        request.setAttribute("paymentMethod", customer.paymentMethod());
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("cartTotal", cartTotal);
        request.setAttribute("cartItemCount", cartItemCount);
        request.setAttribute("isEmpty", cartItems.isEmpty());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserInfo customer = (UserInfo) session.getAttribute("CUSTOMER");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/logout");
            return;
        }

        loadCheckoutData(request, customer);

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/checkout.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        UserInfo customer = (UserInfo) session.getAttribute("CUSTOMER");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/logout");
            return;
        }

        String action = request.getParameter("action");

        try {
            switch (action != null ? action : "") {

                case "order" -> {

                    String selectedMethod = request.getParameter("paymentMethodSlot");
                    if (selectedMethod == null || selectedMethod.trim().isEmpty()) {
                        request.setAttribute("errorMessage", "Välj en betalningsmetod.");
                        loadCheckoutData(request, customer);
                        request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
                        return;
                    }

                    Result result = OrderService.createOrderFromCart(customer.userId());

                    if (result == Result.SUCCESS) {
                        response.sendRedirect(request.getContextPath() + "/currentOrder");
                    } else {
                        request.setAttribute("errorMessage", "Något gick fel när du skulle beställa ordern.");
                        loadCheckoutData(request, customer);
                        request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
                    }
                    break;
                }

                case "addPaymentMethod" -> {
                    response.sendRedirect(request.getContextPath() + "/profile");

                }

                case "back" -> {
                    response.sendRedirect(request.getContextPath() + "/cart");
                }

                default -> {

                }
            }

        } catch (NumberFormatException e) {
            System.err.println("Ogiligt antal: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Cart error: " + e.getMessage());
        }
    }
}