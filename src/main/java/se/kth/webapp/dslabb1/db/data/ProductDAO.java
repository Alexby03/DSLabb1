package se.kth.webapp.dslabb1.db.data;

import se.kth.webapp.dslabb1.bo.models.Product;
import se.kth.webapp.dslabb1.bo.models.enums.Category;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import java.sql.*;
import java.util.*;

/**
 * DAO record for Product operations - matches T_Product table structure
 */
public record ProductDAO(
        String sku,
        String productName,
        String productDescription,
        Category category,
        int quantity,
        double price,
        boolean isRetired
) {

    /**
     * Create a new product
     */
    public static Result createProduct(ProductDAO productDao) {
        String sql = "INSERT INTO T_Product (sku, productName, productDescription, category, quantity, price, isRetired) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productDao.sku);
            stmt.setString(2, productDao.productName);
            stmt.setString(3, productDao.productDescription);
            stmt.setString(4, productDao.category.name());
            stmt.setInt(5, productDao.quantity);
            stmt.setDouble(6, productDao.price);
            stmt.setBoolean(7, productDao.isRetired);

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        } catch (SQLException e) {
            System.err.println("Error creating product: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Find product by SKU
     */
    public static ProductDAO findBySku(String sku) {
        String sql = "SELECT * FROM T_Product WHERE sku = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sku);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ProductDAO(
                            rs.getString("sku"),
                            rs.getString("productName"),
                            rs.getString("productDescription"),
                            Category.valueOf(rs.getString("category")),
                            rs.getInt("quantity"),
                            rs.getDouble("price"),
                            rs.getBoolean("isRetired")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding product by SKU: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get all products
     */
    public static List<ProductDAO> findAll() {
        List<ProductDAO> products = new ArrayList<>();
        String sql = "SELECT * FROM T_Product";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(new ProductDAO(
                        rs.getString("sku"),
                        rs.getString("productName"),
                        rs.getString("productDescription"),
                        Category.valueOf(rs.getString("category")),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getBoolean("isRetired")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error finding all products: " + e.getMessage());
        }

        return products;
    }

    /**
     * Get available products (not retired and in stock)
     */
    public static List<ProductDAO> findAvailableProducts() {
        List<ProductDAO> products = new ArrayList<>();
        String sql = "SELECT * FROM T_Product WHERE isRetired = FALSE AND quantity > 0";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(new ProductDAO(
                        rs.getString("sku"),
                        rs.getString("productName"),
                        rs.getString("productDescription"),
                        Category.valueOf(rs.getString("category")),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getBoolean("isRetired")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error finding available products: " + e.getMessage());
        }

        return products;
    }

    /**
     * Update product stock
     */
    public static Result updateStock(String sku, int newQuantity) {
        String sql = "UPDATE T_Product SET quantity = ? WHERE sku = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setString(2, sku);

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        } catch (SQLException e) {
            System.err.println("Error updating stock: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Convert ProductDAO to domain model Product
     */
    public Product toDomainModel() {
        return new Product(this.sku, this.productName, this.productDescription,
                this.category, this.price, this.quantity, this.isRetired);
    }

    /**
     * Create ProductDAO from domain model Product
     */
    public static ProductDAO fromDomainModel(Product product) {
        return new ProductDAO(
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getQuantity(),
                product.getPrice(),
                product.isRetired()
        );
    }
}