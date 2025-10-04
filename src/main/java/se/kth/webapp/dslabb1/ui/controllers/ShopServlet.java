package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.webapp.dslabb1.bo.models.CartItem;
import se.kth.webapp.dslabb1.bo.models.Customer;
import se.kth.webapp.dslabb1.bo.models.Product;
import se.kth.webapp.dslabb1.bo.models.enums.Category;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.services.CartService;
import se.kth.webapp.dslabb1.bo.services.ProductService;

import java.io.IOException;
import java.util.List;

@WebServlet("/shop")
public class ShopServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchText = request.getParameter("searchText");
        String categoryParam = request.getParameter("category");

        Category selected = null;
        if (categoryParam != null && !categoryParam.isBlank()) {
            try {
                selected = Category.valueOf(categoryParam.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {

            }
        }

        List<Product> products = ProductService.findProductByCategoryAndName(searchText, selected);

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("CUSTOMER");
        int cartItemCount = 0;

        if (customer != null) {
            cartItemCount = CartService.getCartItemCount(customer.getId(), customer.getUserType());
        }

        String errorMessage = request.getParameter("error");
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.getSession().removeAttribute("errorMessage");
        }

        request.setAttribute("products", products);
        request.setAttribute("categories", Category.values());
        request.setAttribute("selectedCategory", selected);
        request.setAttribute("searchText", searchText == null ? "" : searchText);
        request.setAttribute("cartItemCount", cartItemCount);

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/shop.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession();
        Customer customer = (Customer) session.getAttribute("CUSTOMER");

        if (customer == null) {
            resp.sendRedirect(req.getContextPath() + "/logout");
            return;
        }

        String action = req.getParameter("action");

        if ("addToCart".equals(action)) {
            String sku = req.getParameter("sku");
            String quantityStr = req.getParameter("quantity");

            try {

                int quantity = Integer.parseInt(quantityStr);
                CartItem existing = CartService.getCartItem(customer.getId(), sku);
                int existingQty = (existing == null) ? 0 : existing.getQuantity();
                int stock = ProductService.findProductBySKU(sku).getQuantity();

                if (quantity + existingQty > stock) {
                    req.getSession().setAttribute("errorMessage", "Max antal produkter.");
                    resp.sendRedirect(req.getContextPath() + "/shop?error=Antalet+bestallningar+overskrider+lagret");

                    return;
                } else {
                    Result result = CartService.addItemToCart(
                            customer.getId(), sku, quantity, customer.getUserType());

                    if (result == Result.SUCCESS) {
                        req.setAttribute("successMessage", "Produkt tillagd i kundvagnen!");
                    } else if (result == Result.PRIVILEGE) {
                        req.setAttribute("errorMessage", "Du har inte behörighet att lägga till produkter.");
                    } else {
                        req.setAttribute("errorMessage", "Kunde inte lägga till produkt i kundvagnen.");
                    }
                }

            } catch (NumberFormatException e) {
                req.setAttribute("errorMessage", "Ogiltigt antal.");
            }
        }

        resp.sendRedirect(req.getContextPath() + "/shop");
    }
}
