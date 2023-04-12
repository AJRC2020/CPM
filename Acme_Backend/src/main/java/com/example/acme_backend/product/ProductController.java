package com.example.acme_backend.product;

import java.io.File;
import java.util.*;

import javax.crypto.Cipher;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.acme_backend.bodies.Encrypt;

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
    public ResponseEntity<AppProduct> createProduct(@RequestBody Encrypt encryption) throws Exception {
        //Comes Encrypted

        File file = new File("src/main/resources/privatekey.der");

        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        byte[] market_key = Files.readAllBytes(file.toPath());
        
        String info = decryption(encryption.encryption, market_key);

        System.out.println(info);

        String[] infoSplitted = info.split(":");

        AppProduct product = productService.createProduct(infoSplitted[1], Float.parseFloat(infoSplitted[2]), infoSplitted[0]);

        return ResponseEntity.ok().body(product);
    }

    private String decryption(String encrypted, byte[] key) throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
        PrivateKey priKey = kf.generatePrivate(keySpec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        
        return new String(decrypted);
    }
    
}
