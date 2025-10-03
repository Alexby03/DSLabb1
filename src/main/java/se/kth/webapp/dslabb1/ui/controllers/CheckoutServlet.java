package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import se.kth.webapp.dslabb1.bo.models.*;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;
import se.kth.webapp.dslabb1.bo.services.CartService;
import se.kth.webapp.dslabb1.bo.services.OrderService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "checkoutServlet", value = "/checkout")
public class CheckoutServlet extends HttpServlet {

    private void loadCheckoutData(HttpServletRequest request, Customer customer) {
        Cart userCart = CartService.getUserCart(customer.getId(), customer.getUserType());
        List<CartItem> cartItems = (userCart != null) ? userCart.getItems() : List.of();
        double cartTotal = CartService.getCartTotal(customer.getId(), customer.getUserType());
        int cartItemCount = CartService.getCartItemCount(customer.getId(), customer.getUserType());

        request.setAttribute("email", customer.getEmail());
        request.setAttribute("name", customer.getFullName());
        request.setAttribute("address", customer.getAddress());
        request.setAttribute("paymentMethod", customer.getPaymentMethod());
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("cartTotal", cartTotal);
        request.setAttribute("cartItemCount", cartItemCount);
        request.setAttribute("isEmpty", cartItems.isEmpty());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("CUSTOMER");

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
            Customer customer = (Customer) session.getAttribute("CUSTOMER");

            if (customer == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String action = request.getParameter("action");

            try {
                switch (action != null ? action : "") {

                    case "order" -> {
                        String selectedMethod = request.getParameter("paymentMethodSlot");
                        System.out.println("YOU ORDERED.");
                        if (selectedMethod == null || selectedMethod.trim().isEmpty()) {
                            request.setAttribute("errorMessage", "Välj en betalningsmetod.");
                            loadCheckoutData(request, customer);
                            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
                            System.out.println("NO PAYMENT.");
                            return;
                        }
                        //CartService.clearCart(customer.getId(), customer.getUserType());
                        Cart checkoutCart = CartService.getUserCart(customer.getId(), customer.getUserType());
                        Result result = OrderService.createOrderFromCart(customer.getId(), checkoutCart);
                        if(result == Result.SUCCESS){
                            response.sendRedirect(request.getContextPath() + "/currentOrder");
                        }
                        request.setAttribute("errorMessage", "Något gick fel när du skulle beställa ordern.");
                        loadCheckoutData(request, customer);
                        request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
                    }

                    case "addPaymentMethod" -> {
                        response.sendRedirect(request.getContextPath() + "/profile");

                        return;
                    }

                    case "back" -> {
                        response.sendRedirect(request.getContextPath() + "/cart");
                        return;
                    }

                    default -> {

                    }
                }

            } catch (NumberFormatException e) {
                System.err.println("Ogiligt antal: " +  e.getMessage());
            } catch (Exception e) {
                System.err.println("Cart error: " + e.getMessage());
            }
    }
}