package com.example.acme_backend.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.acme_backend.item.AppItem;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<AppProduct> getProducts() {
        return productRepository.findAll();
    }

    public void addItem(AppItem item, String id) {
        AppProduct product = productRepository.findByUuid(id).get(0);

        product.addItem(item);

        productRepository.save(product);

        productRepository.flush();
    }

    public AppProduct createProduct(String name, Float price, String uuid) {
        AppProduct product = new AppProduct(name, price, uuid);

        productRepository.save(product);

        productRepository.flush();

        return product;
    }

    public AppProduct findByUuid(String uuid) {
        return productRepository.findByUuid(uuid).get(0);
    }
}
