package cuong.com.productservice.service;

import cuong.com.productservice.entity.Product;
import cuong.com.productservice.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        System.out.println("===========> [DB HIT] Truy vấn DB cho Product ID: " + id);
        return productRepository.findById(id).orElse(null);
    }

    @CacheEvict(value = "products", key = "#id")
    public Product updateProduct(Long id, Product updatedProduct) {
        System.out.println("===========> [CACHE EVICT] Xóa Cache cũ cho Product ID: " + id);
        Product existing = productRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setName(updatedProduct.getName());
            existing.setDescription(updatedProduct.getDescription());
            existing.setPrice(updatedProduct.getPrice());
            return productRepository.save(existing);
        }
        return null;
    }
}