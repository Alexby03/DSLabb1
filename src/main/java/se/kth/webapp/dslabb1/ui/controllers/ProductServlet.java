package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import se.kth.webapp.dslabb1.bo.models.Product;
import se.kth.webapp.dslabb1.bo.services.ProductService;

import java.io.IOException;

@WebServlet("/product/*")
public class ProductServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String sku = null;
        if (pathInfo != null && pathInfo.length() > 1) sku = pathInfo.substring(1);

        if (sku == null || sku.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "SKU saknas");
            return;
        }

        Product product = ProductService.findProductBySKU(sku);
        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Produkten hittades inte");
            return;
        }

        request.setAttribute("product", product);
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/product.jsp");
        rd.forward(request, response);
    }
}
