package se.kth.webapp.dslabb1.db.data;

import se.kth.webapp.dslabb1.bo.models.Product;
import se.kth.webapp.dslabb1.bo.models.enums.Category;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.db.DBManager;
import se.kth.webapp.dslabb1.db.DataAccessException;
import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for an entity in table T_Product in DB schema.
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
     * Generates a new product to the database.
     *
     * @param productDao instance of a product data access object.
     * @return whether creating a product was successful or not.
     */
    public static Result createProduct(ProductDAO productDao) {
        String sql = "INSERT INTO T_Product (sku, productName, productDescription, category, quantity, price, isRetired) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (DBManager db = DBManager.open();
             PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {

            stmt.setString(1, productDao.sku);
            stmt.setString(2, productDao.productName);
            stmt.setString(3, productDao.productDescription);
            stmt.setString(4, productDao.category.name());
            stmt.setInt(5, productDao.quantity);
            stmt.setBigDecimal(6, BigDecimal.valueOf(productDao.price));
            stmt.setBoolean(7, productDao.isRetired);

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        } catch (SQLException e) {
            System.err.println("Error creating product: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Finds a user in the database and returns it.
     *
     * @param sku the queried ID of the product.
     * @return the product data access object if found, otherwise null.
     */
    public static ProductDAO findBySku(String sku) {
        String sql = "SELECT * FROM T_Product WHERE sku = ?";

        try (DBManager db = DBManager.open();
             PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {

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
     * Attempts to find a product by category and / or name.
     *
     * @param category    enum, product category, enter null if all.
     * @param productName string, name of product, enter null if no specific name search.
     * @return list of products matching the query.
     */
    public static List<ProductDAO> findByCategoryAndName(Category category, String productName) {
        List<ProductDAO> products = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM T_Product WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (category != null) {
            sql.append(" AND category = ?");
            params.add(category.name());
        }
        if (productName != null && !productName.isBlank()) {
            sql.append(" AND productName LIKE ?");
            params.add("%" + productName.trim() + "%");
        }

        try (DBManager db = DBManager.open();
             PreparedStatement stmt = db.getConnection().prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                stmt.setObject(i + 1, p);
            }

            try (ResultSet rs = stmt.executeQuery()) {
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
            }
        } catch (SQLException e) {
            System.err.println("Error finding products by category or name: " + e.getMessage());
        }
        return products;
    }

    /**
     * Attempts to find all products currently existing.
     *
     * @return list of product data access objects of found products.
     */
    public static List<ProductDAO> findAll() {
        List<ProductDAO> products = new ArrayList<>();
        String sql = "SELECT * FROM T_Product";

        try (DBManager db = DBManager.open();
             PreparedStatement stmt = db.getConnection().prepareStatement(sql);
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
     * Attempts to find all products currently not retired and in stock.
     *
     * @return list of product data access objects of found products.
     */
    public static List<ProductDAO> findAvailableProducts() {
        List<ProductDAO> products = new ArrayList<>();
        String sql = "SELECT * FROM T_Product WHERE isRetired = FALSE AND quantity > 0";

        try (DBManager db = DBManager.open();
             PreparedStatement stmt = db.getConnection().prepareStatement(sql);
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
     * Updates a product's quantity within the database.
     *
     * @param sku         the unique ID of the product.
     * @param newQuantity of the product.
     * @param conn        the connection to the database.
     * @return whether updating the product was successful or not.
     * @throws SQLException if updating the product failed.
     */
    public static Result updateStock(String sku, int newQuantity, Connection conn) throws DataAccessException {
        String sql = "UPDATE T_Product SET quantity = ? WHERE sku = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setString(2, sku);

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Updates a product's price within the database.
     *
     * @param sku      the unique ID of the product.
     * @param newPrice of the product.
     * @return whether updating the product was successful or not.
     */
    public static Result changePrice(String sku, double newPrice) {
        String sql = "UPDATE T_Product SET price = ? WHERE sku = ?";

        try (DBManager db = DBManager.open();
             PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {

            stmt.setBigDecimal(1, BigDecimal.valueOf(newPrice));
            stmt.setString(2, sku);

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        } catch (SQLException e) {
            System.err.println("Error updating Price: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Converts a product instance to a product data access object.
     *
     * @return the product data access object.
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

    /**
     * Converts a product data access object to a product instance.
     *
     * @return the product instance.
     */
    public Product toDomainModel() {
        return new Product(this.sku, this.productName, this.productDescription,
                this.category, this.quantity, this.price, this.isRetired);
    }
}