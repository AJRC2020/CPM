package com.example.acme_backend.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
        extends JpaRepository<AppUser,Long> {

                List<AppUser> findByUuid(String uuid);

                List<AppUser> findByUsername(String username);
}
