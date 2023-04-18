package com.example.acme_backend.bodies;

import java.util.List;
import java.util.Optional;

public class NewPurchase {
    public List<ProductReceipt> products;
    public String user_id;
    public Boolean discount;
    public Optional<String> voucher_id;
}
