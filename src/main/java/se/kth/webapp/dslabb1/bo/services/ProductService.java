package se.kth.webapp.dslabb1.bo.services;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.exceptions.ClosedOnExpiredPasswordException;
import se.kth.webapp.dslabb1.bo.models.Product;
import se.kth.webapp.dslabb1.bo.models.enums.Category;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;
import se.kth.webapp.dslabb1.db.DBManager;
import se.kth.webapp.dslabb1.db.data.ProductDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class providing methods for handling products to the presentation layer.
 */
public class ProductService {

    /**
     * Generates a new product to the database.
     * @param newProduct instance of a product.
     * @param userType whether the user is an admin or not.
     * @return whether creating a product was successful or not.
     */
    public static Result registerProduct(Product newProduct, UserType userType) {
        if(!UserType.ADMIN.equals(userType)) return Result.PRIVILEGE;
        if (newProduct == null || newProduct.getSku() == null || newProduct.getSku().isBlank()) return Result.FAILED;
        if (newProduct.getPrice() < 0 || newProduct.getQuantity() < 0) return Result.FAILED;

        return ProductDAO.createProduct(new ProductDAO(newProduct.getSku(),
                newProduct.getName(), newProduct.getDescription(),
                newProduct.getCategory(), newProduct.getQuantity(),
                newProduct.getPrice(), newProduct.isRetired()));
    }

    /**
     * Attempts to find a product by category and / or name.
     * @param category enum, product category.
     * @param productName string, name of product.
     * @return list of products matching the query.
     */
    public static List<Product> findProductByCategoryAndName(String productName, Category category) {
        try{
            List<ProductDAO> foundProducts = ProductDAO.findByCategoryAndName(category, productName);
            List<Product> products = new ArrayList<>();
            for(ProductDAO productDAO : foundProducts){
                products.add(new Product(productDAO.sku(), productDAO.productName(), productDAO.productDescription(),
                        productDAO.category(), productDAO.quantity(), productDAO.price(), productDAO.isRetired()));
            }
            return products;
        } catch (Exception e) {
            System.err.println("Error finding product by NAME " + productName);
            return null;
        }
    }

    /**
     * Increases the quantity of the product.
     * @param product
     * @param plusQuantity
     * @param userType
     * @return whether increasing the quantity was successful or not.
     */
    public static Result increaseQuantity(Product product, int plusQuantity, UserType userType) {
        if(!UserType.ADMIN.equals(userType)) return Result.PRIVILEGE;

        product.increaseQuantity(plusQuantity);
        try (Connection conn = DBManager.getConnection()) {
            return ProductDAO.updateStock(product.getSku(), product.getQuantity(), conn);
        } catch (SQLException e) {
            System.err.println("Error increasing stock " + product.getSku());
            return Result.FAILED;
        }
    }

    /**
     * Decreases the quantity of the product.
     * @param product
     * @param minusQuantity
     * @return whether decreasing the quantity was successful or not.
     */
    public static Result decreaseQuantity(Product product, int minusQuantity) {
        product.decreaseQuantity(minusQuantity);
        try (Connection conn = DBManager.getConnection()) {
            return ProductDAO.updateStock(product.getSku(), product.getQuantity(), conn);
        } catch (SQLException e) {
            System.err.println("Error decreasing stock " + product.getQuantity());
            return Result.FAILED;
        }
    }

    /**
     * Attempts to change the price of the product.
     * @param product instance of the product.
     * @param newPrice
     * @param userType
     * @return whether the price change was successful or not.
     */
    public static Result changePrice(Product product, double newPrice, UserType userType) {
        if(!UserType.ADMIN.equals(userType)) return Result.PRIVILEGE;
        return ProductDAO.changePrice(product.getSku(), newPrice);
    }

    /**
     * Check whether the product is in stock.
     * @param product
     * @return true if in stock. False if not.
     */
    public static boolean isInStock(Product product) {
        return product.getQuantity() > 0;
    }

    /**
     * Check whether the product is retired.
     * @param product
     * @return true if retired. False if not.
     */
    public static boolean isRetired(Product product) {
        return product.isRetired();
    }

    /**
     * Attempts to find a product by its SKU.
     * @param sku
     * @return product instance if found, else null.
     */
    public static Product findProductBySKU(String sku){
        if (sku == null || sku.isBlank()) return null;

        try{
            ProductDAO foundSKU = ProductDAO.findBySku(sku);
            return foundSKU != null ? foundSKU.toDomainModel() : null;
        } catch (Exception e) {
            System.err.println("Error finding product by SKU " + sku);
            return null;
        }
    }

    /**
     * Attempts to retrieve all products.
     * @return a list of all products registered in the database.
     */
    public static List<Product> getAllProducts() {
        try {
            List<ProductDAO> foundProducts = ProductDAO.findAll();
            List<Product> products = new ArrayList<>();
            for(ProductDAO productDAO : foundProducts)
            {
                products.add(new Product(productDAO.sku(), productDAO.productName(), productDAO.productDescription(),
                        productDAO.category(), productDAO.quantity(), productDAO.price(), productDAO.isRetired()));
            }
            return products;
        } catch (Exception e) {
            System.err.println("Error getting all products: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
