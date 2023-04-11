package com.example.acme_backend.product;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.acme_backend.bodies.NewProduct;

@RestController
@RequestMapping(path = "api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<AppProduct> getProducts() {
        return productService.getProducts();
    }

    //TODO: this is supposed to be an encrypted QRCODE I think
    @PostMapping("/new")
    public AppProduct createProduct(@RequestBody NewProduct info) {
        UUID uuid = UUID.randomUUID();

        AppProduct product = productService.createProduct(info.name, info.price, uuid.toString());

        return product;
    }
    
}
