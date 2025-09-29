package se.kth.webapp.dslabb1.models;

import se.kth.webapp.dslabb1.models.enums.Category;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Product record keyed by SKU (primary key).
 *
 */
public class Product implements Serializable {
    private String sku;           // primary key, custom and unique for entire product
    private String name;
    private String description;
    private Category category;
    private int quantity;
    private BigDecimal price;
    private boolean retired;

    public Product(String sku, String name, String description, Category category, BigDecimal price, int quantity) {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku required");
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price != null ? price : BigDecimal.ZERO;
        this.quantity = Math.max(0, quantity);
        this.retired = false;
    }


    public String getSku() { return sku; }
    public void setSku(String sku) { if (sku == null) throw new IllegalArgumentException("sku"); this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public int getQuantity() { return quantity; }


    public void increaseQuantity(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount > 0");
        this.quantity += amount;
    }


    public boolean decreaseQuantity(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount > 0");
        if (this.quantity < amount) return false;
        this.quantity -= amount;
        return true;
    }

    public boolean isInStock(int required) {
        return required > 0 && this.quantity >= required;
    }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isRetired() { return retired; }
    public void setRetired(boolean retired) { this.retired = retired; }

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
