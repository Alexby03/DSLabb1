package se.kth.webapp.dslabb1.ui.info;

import se.kth.webapp.dslabb1.bo.models.enums.Category;
import java.io.Serializable;


public record ProductInfo(String sku, String name, String description, Category category, int quantity,
                          double price, boolean retired) implements Serializable {


    public ProductInfo {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku required");
    }
}

