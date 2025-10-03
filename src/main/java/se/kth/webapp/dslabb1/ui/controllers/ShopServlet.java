package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.util.List;

import se.kth.webapp.dslabb1.bo.models.Customer;
import se.kth.webapp.dslabb1.bo.models.enums.Category;
import se.kth.webapp.dslabb1.bo.models.Product;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.services.CartService;
import se.kth.webapp.dslabb1.bo.services.ProductService;

@WebServlet("/shop")
public class ShopServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String searchText = req.getParameter("searchText");
        String categoryParam = req.getParameter("category");

        Category selected = null;
        if (categoryParam != null && !categoryParam.isBlank()) {
            try {
                selected = Category.valueOf(categoryParam.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {

            }
        }

        List<Product> products = ProductService.findProductByCategoryAndName(searchText, selected);

        // Get cart item count
        HttpSession session = req.getSession();
        Customer customer = (Customer) session.getAttribute("CUSTOMER");
        int cartItemCount = 0;

        if (customer != null) {
            cartItemCount = CartService.getCartItemCount(customer.getId(), customer.getUserType());
        }

        req.setAttribute("products", products);
        req.setAttribute("categories", Category.values());
        req.setAttribute("selectedCategory", selected);
        req.setAttribute("searchText", searchText == null ? "" : searchText);
        req.setAttribute("cartItemCount", cartItemCount);

        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/views/shop.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        Customer customer = (Customer) session.getAttribute("CUSTOMER");

        if (customer == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");

        if ("addToCart".equals(action)) {
            String sku = req.getParameter("sku");
            String quantityStr = req.getParameter("quantity");

            try {
                int quantity = Integer.parseInt(quantityStr);

                Result result = CartService.addItemToCart(
                        customer.getId(), sku, quantity, customer.getUserType());

                if (result == Result.SUCCESS) {
                    req.setAttribute("successMessage", "Produkt tillagd i kundvagnen!");
                } else if (result == Result.PRIVILEGE) {
                    req.setAttribute("errorMessage", "Du har inte behörighet att lägga till produkter.");
                } else {
                    req.setAttribute("errorMessage", "Kunde inte lägga till produkt i kundvagnen.");
                }

            } catch (NumberFormatException e) {
                req.setAttribute("errorMessage", "Ogiltigt antal.");
            }
        }

        // Redirect to avoid form resubmission
        resp.sendRedirect(req.getContextPath() + "/shop");
    }
}
