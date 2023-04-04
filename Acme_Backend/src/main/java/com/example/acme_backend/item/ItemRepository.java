package com.example.acme_backend.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository 
        extends JpaRepository<AppItem, Long> {
    
}
