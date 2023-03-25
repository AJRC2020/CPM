package com.example.acme_backend.purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository 
        extends JpaRepository<AppPurchase, Long> {
    
}
