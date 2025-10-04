package se.kth.webapp.dslabb1.bo.models;

import se.kth.webapp.dslabb1.bo.models.enums.Category;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class representing the Product class and object.
 */
public class Product implements Serializable {
    private final String sku;           // primary key, custom and unique for entire product
    private String name;
    private String description;
    private Category category;
    private int quantity;
    private double price;
    private boolean retired;

    /**
     * Recreates a product from the database.
     *
     * @param sku         unique ID for the product
     * @param name        of the product.
     * @param description of the product.
     * @param category    of the product.
     * @param quantity    in stock for the product.
     * @param price       of the product.
     * @param retired     is the product retired?
     */
    public Product(String sku, String name, String description, Category category, int quantity, double price, boolean retired) {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku required");
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.retired = retired;
    }


    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Increases the quantity of the product stock.
     *
     * @param amount to increase
     */
    public void increaseQuantity(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be > 0");
        this.quantity += amount;
    }

    /**
     * Decreases the quantity of the product stock.
     *
     * @param amount to decrease
     */
    public boolean decreaseQuantity(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be > 0");
        if (this.quantity < amount) return false;
        this.quantity -= amount;
        return true;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price >= 0.0 ? price : this.price;
    }

    public boolean isRetired() {
        return retired;
    }

    /**
     * Sets the state of this product instance as Retired.
     */
    public void retire() {
        this.retired = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(sku, product.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }

    @Override
    public String toString() {
        return "Product{" + "sku='" + sku + '\'' + ", name='" + name + '\'' + ", price=" + price + ", qty=" + quantity + '}';
    }
}
