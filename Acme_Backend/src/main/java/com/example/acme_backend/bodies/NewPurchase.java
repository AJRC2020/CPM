package com.example.acme_backend.bodies;

import java.util.List;
import java.util.Optional;

public class NewPurchase {
    public List<ProductAndQuantity> products;
    public String user_id;
    public Boolean discount;
    public Optional<String> voucher_id;

    public String toString() {
        String retString = "{";
        if (voucher_id.isPresent()) {
            retString += "\"voucher_id\":\"" + voucher_id.get() + "\",";
        }
        else {
            retString += "\"voucher_id\":null,"; 
        }

        retString += "\"discount\":" + discount.toString() + ",\"user_id\":\"" + user_id + "\",\"products\":[";

        String items = "";
        for (int i = 0; i < products.size(); i++) {
            items += "{\"quantity\":" + products.get(i).quantity.toString() + ",\"product\":\"" + products.get(i).product + "\"}";

            if (i != products.size() - 1) {
                items += ",";
            }
        }

        retString += items + "]}";

        return retString;
    }
}
