package se.kth.webapp.dslabb1.bo.services;

import se.kth.webapp.dslabb1.bo.models.Product;
import se.kth.webapp.dslabb1.bo.models.enums.Category;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;
import se.kth.webapp.dslabb1.db.DBManager;
import se.kth.webapp.dslabb1.db.DataAccessException;
import se.kth.webapp.dslabb1.db.data.ProductDAO;
import se.kth.webapp.dslabb1.ui.info.ProductInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class providing methods for handling products to the presentation layer.
 */
public class ProductService {

    public static ProductInfo toProductInfo(Product product) {
        return new ProductInfo(product.getSku(),product.getName(),product.getDescription(), product.getCategory(), product.getQuantity(),
                product.getPrice(), product.isRetired());
    }

    /**
     * Generates a new product to the database.
     * @param newProduct instance of a product.
     * @param userType whether the user is an admin or not.
     * @return whether creating a product was successful or not.
     */
    public static Result registerProduct(ProductInfo newProduct, UserType userType) {
        if(!UserType.ADMIN.equals(userType)) return Result.PRIVILEGE;
        if (newProduct == null || newProduct.sku() == null || newProduct.sku().isBlank()) return Result.FAILED;
        if (newProduct.price() < 0 || newProduct.quantity() < 0) return Result.FAILED;

        return ProductDAO.createProduct(new ProductDAO(newProduct.sku(),
                newProduct.name(), newProduct.description(),
                newProduct.category(), newProduct.quantity(),
                newProduct.price(), newProduct.retired())
        );
    }

    /**
     * Attempts to find a product by category and / or name.
     * @param category enum, product category.
     * @param productName string, name of product.
     * @return list of products matching the query.
     */
    public static List<ProductInfo> findProductByCategoryAndName(String productName, Category category) {
        try {
            List<ProductDAO> foundProducts = ProductDAO.findByCategoryAndName(category, productName);
            return foundProducts.stream()
                    .map(ProductDAO::toDomainModel)
                    .map(ProductService::toProductInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding product by NAME: " + productName);
            return new ArrayList<>();
        }
    }

    /**
     * Increases the quantity of the product.
     * @param sku
     * @param plusQuantity
     * @param userType
     * @return whether increasing the quantity was successful or not.
     */
    public static Result increaseQuantity(String sku, int plusQuantity, UserType userType) {
        if(!UserType.ADMIN.equals(userType)) return Result.PRIVILEGE;
        ProductDAO foundSKU = ProductDAO.findBySku(sku);
        if (foundSKU == null)  return Result.FAILED;
        Product product = foundSKU.toDomainModel();
        product.increaseQuantity(plusQuantity);
        try (DBManager db = DBManager.open()) {
            return ProductDAO.updateStock(product.getSku(), product.getQuantity(), db.getConnection());
        } catch (DataAccessException e) {
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
        try (DBManager db = DBManager.open()) {
            return ProductDAO.updateStock(product.getSku(), product.getQuantity(), db.getConnection());
        } catch (DataAccessException e) {
            System.err.println("Error decreasing stock " + product.getQuantity());
            return Result.FAILED;
        }
    }

    /**
     * Attempts to change the price of the product.
     * @param sku Instance of sku belonging to a product
     * @param newPrice
     * @param userType
     * @return whether the price change was successful or not.
     */
    public static Result changePrice(String sku, double newPrice, UserType userType) {
        if (!UserType.ADMIN.equals(userType))
            return Result.PRIVILEGE;
        if (sku == null || sku.isBlank())
            return Result.FAILED;

        return ProductDAO.changePrice(sku, newPrice);
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
    public static ProductInfo findProductBySKU(String sku) {
        if (sku == null || sku.isBlank()) return null;
        try {
            ProductDAO foundSKU = ProductDAO.findBySku(sku);
            if (foundSKU != null) {
                Product product = foundSKU.toDomainModel();
                return toProductInfo(product);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error finding product by SKU: " + sku);
            return null;
        }
    }

    /**
     * Attempts to retrieve all products.
     * @return a list of all products registered in the database.
     */
    public static List<ProductInfo> getAllProducts() {
        try {
            List<ProductDAO> foundProducts = ProductDAO.findAll();
            return foundProducts.stream()
                    .map(ProductDAO::toDomainModel)
                    .map(ProductService::toProductInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting all products: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
