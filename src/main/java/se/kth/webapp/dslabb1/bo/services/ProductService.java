package se.kth.webapp.dslabb1.bo.services;

import se.kth.webapp.dslabb1.bo.models.Product;
import se.kth.webapp.dslabb1.bo.models.enums.Category;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;
import se.kth.webapp.dslabb1.db.data.ProductDAO;

import java.util.ArrayList;
import java.util.List;

public class ProductService {

    public Result registerProduct(Product newProduct, UserType userType) {
        if(!UserType.ADMIN.equals(userType)) return Result.PRIVILEGE;
        if (newProduct == null || newProduct.getSku() == null || newProduct.getSku().isBlank()) return Result.FAILED;
        if (newProduct.getPrice() < 0 || newProduct.getQuantity() < 0) return Result.FAILED;

        return ProductDAO.createProduct(new ProductDAO(newProduct.getSku(),
                newProduct.getName(), newProduct.getDescription(),
                newProduct.getCategory(), newProduct.getQuantity(),
                newProduct.getPrice(), newProduct.isRetired()));
    }

    public List<Product> findProductByCategoryAndName(String search, Category category) {
        if (search == null || search.isBlank()) return null;
        try{
            List<ProductDAO> foundProducts = ProductDAO.findByCategoryAndName(category, search);
            List<Product> products = new ArrayList<>();
            for(ProductDAO productDAO : foundProducts){
                products.add(new Product(productDAO.sku(), productDAO.productName(), productDAO.productDescription(),
                        productDAO.category(), productDAO.quantity(), productDAO.price(), productDAO.isRetired()));
            }
            return products;
        } catch (Exception e) {
            System.err.println("Error finding product by NAME " + search);
            return null;
        }
    }

    public Result increaseQuantity(Product product, int plusQuantity, UserType userType) {
        if(!UserType.ADMIN.equals(userType)) return Result.PRIVILEGE;

        product.increaseQuantity(plusQuantity);
        return ProductDAO.updateStock(product.getSku(), product.getQuantity());
    }

    public Result decreaseQuantity(Product product, int minusQuantity) {
        product.decreaseQuantity(minusQuantity);
        return ProductDAO.updateStock(product.getSku(), product.getQuantity());
    }

    public Result changePrice(Product product, double newPrice, UserType userType) {
        if(!UserType.ADMIN.equals(userType)) return Result.PRIVILEGE;
        return ProductDAO.changePrice(product.getSku(), newPrice);
    }

    public boolean isInStock(Product product) {
        return product.getQuantity() > 0;
    }

    public boolean isRetired(Product product) {
        return product.isRetired();
    }

    public Product findProductBySKU(String sku){
        if (sku == null || sku.isBlank()) return null;

        try{
            ProductDAO foundSKU = ProductDAO.findBySku(sku);
            return foundSKU != null ? foundSKU.toDomainModel() : null;
        } catch (Exception e) {
            System.err.println("Error finding product by SKU " + sku);
            return null;
        }
    }

    public List<Product> getAllProducts() {
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
