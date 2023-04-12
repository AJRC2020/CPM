package com.example.acme_backend.voucher;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.acme_backend.user.AppUser;

@Service
public class VoucherService {
    
    private final VoucherRepository voucherRepository;

    @Autowired
    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public List<AppVoucher> getVouchers() {
        return voucherRepository.findAll();
    }

    public AppVoucher usedVoucher(String uuid) {
        AppVoucher voucher = voucherRepository.findByUuid(uuid).get(0);
        voucher.setUsed(true);
        voucherRepository.save(voucher);
        voucherRepository.flush();

        return voucher;
    }

    public AppVoucher createVoucher(AppUser user) {
        UUID uuid = UUID.randomUUID();

        LocalDate date = LocalDate.now();

        AppVoucher voucher = new AppVoucher(uuid.toString(), Date.valueOf(date));

        voucher.setUser(user);

        voucherRepository.save(voucher);

        voucherRepository.flush();

        return voucher;
    }
}
