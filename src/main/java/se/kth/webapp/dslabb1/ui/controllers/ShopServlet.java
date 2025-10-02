package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.util.List;

import se.kth.webapp.dslabb1.bo.models.enums.Category;
import se.kth.webapp.dslabb1.bo.models.Product;
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

        req.setAttribute("products", products);
        req.setAttribute("categories", Category.values());
        req.setAttribute("selectedCategory", selected);
        req.setAttribute("searchText", searchText == null ? "" : searchText);

        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/views/shop.jsp");
        rd.forward(req, resp);
    }
}
