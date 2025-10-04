package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.webapp.dslabb1.bo.models.Cart;
import se.kth.webapp.dslabb1.bo.models.CartItem;
import se.kth.webapp.dslabb1.bo.models.Customer;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.services.CartService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "cartServlet", value = "/cart")
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("CUSTOMER");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Cart userCart = CartService.getUserCart(customer.getId(), customer.getUserType());
        List<CartItem> cartItems = (userCart != null) ? userCart.items() : List.of();
        double cartTotal = CartService.getCartTotal(customer.getId(), customer.getUserType());
        int cartItemCount = CartService.getCartItemCount(customer.getId(), customer.getUserType());

        request.setAttribute("cartItems", cartItems);
        request.setAttribute("cartTotal", cartTotal);
        request.setAttribute("cartItemCount", cartItemCount);
        request.setAttribute("isEmpty", cartItems.isEmpty());

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/cart.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("CUSTOMER");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        Result result = Result.FAILED;
        String successMessage = null;
        String errorMessage = null;

        try {
            switch (action != null ? action : "") {
                case "updateQuantity" -> {
                    String sku = request.getParameter("sku");
                    String quantityStr = request.getParameter("quantity");

                    if (sku != null && quantityStr != null) {
                        int quantity = Integer.parseInt(quantityStr);
                        result = CartService.updateCartItemQuantity(
                                customer.getId(), sku, quantity, customer.getUserType());

                        if (result == Result.SUCCESS) {
                            successMessage = "Cart has updated!";
                        } else {
                            errorMessage = "Kunde inte uppdatera antalet.";
                        }
                    }
                }

                case "removeItem" -> {
                    String sku = request.getParameter("sku");
                    if (sku != null) {
                        result = CartService.removeItemFromCart(
                                customer.getId(), sku, customer.getUserType());

                        if (result == Result.SUCCESS) {
                            successMessage = "Produkt borttagen från kundvagnen!";
                        } else {
                            errorMessage = "Kunde inte ta bort produkten.";
                        }
                    }
                }

                case "clearCart" -> {
                    result = CartService.clearCart(customer.getId(), customer.getUserType());

                    if (result == Result.SUCCESS) {
                        successMessage = "Kundvagnen är nu tom!";
                    } else {
                        errorMessage = "Kunde inte tömma kundvagnen.";
                    }
                }

                case "checkout" -> {
                    response.sendRedirect(request.getContextPath() + "/checkout");
                    return;
                }

                default -> {
                    errorMessage = "Invalid Move!!!";
                }
            }

        } catch (NumberFormatException e) {
            errorMessage = "Ogiltigt antal.";
        } catch (Exception e) {
            errorMessage = "Ett fel uppstod. Försök igen.";
            System.err.println("Cart error: " + e.getMessage());
        }

        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
        }
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }

        // Reload cart data after operation
        Cart userCart = CartService.getUserCart(customer.getId(), customer.getUserType());
        List<CartItem> cartItems = (userCart != null) ? userCart.items() : List.of();
        double cartTotal = CartService.getCartTotal(customer.getId(), customer.getUserType());
        int cartItemCount = CartService.getCartItemCount(customer.getId(), customer.getUserType());

        request.setAttribute("cartItems", cartItems);
        request.setAttribute("cartTotal", cartTotal);
        request.setAttribute("cartItemCount", cartItemCount);
        request.setAttribute("isEmpty", cartItems.isEmpty());

        // Forward back to cart JSP with messages
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/cart.jsp");
        rd.forward(request, response);
    }
}