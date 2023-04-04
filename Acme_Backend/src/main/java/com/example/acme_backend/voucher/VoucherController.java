package com.example.acme_backend.voucher;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/vouchers")
public class VoucherController {
    
    private final VoucherService voucherService;

    @Autowired
    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public List<AppVoucher> getVouchers() {
        return voucherService.getVouchers();
    }

}
