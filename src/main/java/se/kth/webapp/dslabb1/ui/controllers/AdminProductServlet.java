package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.webapp.dslabb1.bo.models.enums.Category;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.services.ProductService;
import se.kth.webapp.dslabb1.ui.info.ProductInfo;
import se.kth.webapp.dslabb1.ui.info.UserInfo;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/products")
public class AdminProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ADMIN") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserInfo admin = (UserInfo) session.getAttribute("ADMIN");
        List<ProductInfo> products = ProductService.getAllProducts();

        request.setAttribute("admin", admin);
        request.setAttribute("products", products);
        request.setAttribute("categories", Category.values());

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/admin/products.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ADMIN") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserInfo admin = (UserInfo) session.getAttribute("ADMIN");
        String action = request.getParameter("action");

        try {
            switch (action != null ? action : "") {
                case "create" -> createProduct(request, admin);
                case "updateStock" -> updateStock(request, admin);
                case "updatePrice" -> updatePrice(request, admin);
                default -> request.setAttribute("errorMessage", "Error");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Ett fel uppstod: " + e.getMessage());
        }

        doGet(request, response);
    }

    private void createProduct(HttpServletRequest request, UserInfo admin) {
        String sku = request.getParameter("sku");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String categoryStr = request.getParameter("category");
        String quantityStr = request.getParameter("quantity");
        String priceStr = request.getParameter("price");

        try {
            Category category = Category.valueOf(categoryStr);
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);

            ProductInfo newProduct = new ProductInfo(sku, name, description, category, quantity, price, false);
            Result result = ProductService.registerProduct(newProduct, admin.userType());

            if (result == Result.SUCCESS) {
                request.setAttribute("successMessage", "Produkt skapad.");
            } else {
                request.setAttribute("errorMessage", "Kunde inte skapa produkten.");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Ogiltiga produktdata: " + e.getMessage());
        }
    }

    private void updateStock(HttpServletRequest request, UserInfo admin) {
        String sku = request.getParameter("sku");
        String quantityStr = request.getParameter("quantity");

        try {
            int quantity = Integer.parseInt(quantityStr);
            ProductInfo product = ProductService.findProductBySKU(sku);

            if (product != null) {
                Result result = ProductService.increaseQuantity(sku, quantity, admin.userType());  //TODO: check if works with sku
                if (result == Result.SUCCESS) {
                    request.setAttribute("successMessage", "Lager uppdaterat.");
                } else {
                    request.setAttribute("errorMessage", "Kunde inte uppdatera lagret.");
                }
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Ogiltiga lagerdata: " + e.getMessage());
        }
    }

    private void updatePrice(HttpServletRequest request, UserInfo admin) {
        String sku = request.getParameter("sku");
        String priceStr = request.getParameter("price");

        try {
            double price = Double.parseDouble(priceStr);
            ProductInfo product = ProductService.findProductBySKU(sku);

            if (product != null) {
                Result result = ProductService.changePrice(sku, price, admin.userType());
                if (result == Result.SUCCESS) {
                    request.setAttribute("successMessage", "Pris uppdaterat.");
                } else {
                    request.setAttribute("errorMessage", "Kunde inte uppdatera priset.");
                }
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Ogiltigt pris: " + e.getMessage());
        }
    }
}
