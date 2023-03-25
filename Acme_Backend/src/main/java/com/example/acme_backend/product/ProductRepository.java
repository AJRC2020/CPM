package com.example.acme_backend.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository 
    extends JpaRepository<AppProduct, Long> {

        List<AppProduct> findByUuid(String uuid);
    
}
