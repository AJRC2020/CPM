package com.example.acme_backend.item;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.acme_backend.product.AppProduct;
import com.example.acme_backend.purchase.AppPurchase;

@Service
public class ItemService {
    
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<AppItem> getItems() {
        return itemRepository.findAll();
    }

    public AppItem createItem(Integer quantity, AppProduct product, AppPurchase purchase) {
        AppItem item = new AppItem(quantity, product, purchase);

        itemRepository.save(item);

        itemRepository.flush();

        return item;
    }
}
