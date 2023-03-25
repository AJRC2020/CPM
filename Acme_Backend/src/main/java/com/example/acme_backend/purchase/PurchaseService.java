package com.example.acme_backend.purchase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.acme_backend.user.AppUser;
import com.example.acme_backend.item.AppItem;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {
    
    private final PurchaseRepository purchaseRepository;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public List<AppPurchase> getPurchases() {
        return purchaseRepository.findAll();
    }

    public AppPurchase createPurchase() {
        AppPurchase purchase = new AppPurchase();

        purchaseRepository.save(purchase);

        purchaseRepository.flush();

        return purchase;
    }

    public void addItem(AppItem item, Long id) {
        Optional<AppPurchase> purchase = purchaseRepository.findById(id);
        AppPurchase update_purchase = purchase.get();

        update_purchase.addItem(item);

        purchaseRepository.save(update_purchase);

        purchaseRepository.flush();
    }

    public AppPurchase updatePurchase(Float total, Date date, Boolean discount, AppUser user, Long id) {
        Optional<AppPurchase> purchase = purchaseRepository.findById(id);
        AppPurchase update_purchase = purchase.get();

        update_purchase.setDate(date);
        update_purchase.setPrice(total);
        update_purchase.setVoucher(discount);
        update_purchase.setUser(user);

        purchaseRepository.save(update_purchase);

        purchaseRepository.flush();

        return update_purchase;
    }
}
