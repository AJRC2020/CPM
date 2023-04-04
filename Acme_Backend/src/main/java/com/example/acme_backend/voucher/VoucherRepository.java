package com.example.acme_backend.voucher;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VoucherRepository 
        extends JpaRepository<AppVoucher, Long> {

                List<AppVoucher> findByUuid(String uuid);
}
