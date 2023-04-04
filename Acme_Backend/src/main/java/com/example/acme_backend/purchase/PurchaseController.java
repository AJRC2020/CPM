package com.example.acme_backend.purchase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.acme_backend.bodies.NewPurchase;
import com.example.acme_backend.bodies.ProductAndQuantity;
import com.example.acme_backend.bodies.ReturnPurchase;
import com.example.acme_backend.bodies.SignedNewPurchase;
import com.example.acme_backend.item.ItemService;
import com.example.acme_backend.product.AppProduct;
import com.example.acme_backend.product.ProductService;
import com.example.acme_backend.user.AppUser;
import com.example.acme_backend.user.UserService;
import com.example.acme_backend.voucher.VoucherService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

@RestController
@RequestMapping(path = "api/purchases")
public class PurchaseController {
    
    private final PurchaseService purchaseService;
    private final VoucherService voucherService;
    private final UserService userService;
    private final ItemService itemService;
    private final ProductService productService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService, VoucherService voucherService, UserService userService, ItemService itemService, ProductService productService) {
        this.purchaseService = purchaseService;
        this.voucherService = voucherService;
        this.userService = userService;
        this.itemService = itemService;
        this.productService = productService;
    }

    @GetMapping
    public List<AppPurchase> getPurchases() {
        return purchaseService.getPurchases();
    }

    @PostMapping("/new")
    public ReturnPurchase createPurchase(@RequestBody SignedNewPurchase signedContent) throws Exception {
        Float total = 0.0f;
        NewPurchase content = signedContent.purchase;
        AppUser user = userService.getByUuid(content.user_id);

        if (!verifySignature(signedContent.signature, content, user.getPublic_key())){
            return null;
        }

        AppPurchase purchase = purchaseService.createPurchase();
        
        for (ProductAndQuantity products : content.products) {
            AppProduct product = productService.findByUuid(products.product);
            total += product.getPrice() * products.quantity;

            itemService.createItem(products.quantity, product, purchase);
            products.product = product.getName();
        }   

        if (content.discount) {
            total -= userService.getByUuid(content.user_id).getDiscount();
            userService.updateDiscount(content.user_id, 0.0f);
            if (total < 0) {
                userService.updateDiscount(content.user_id, -total);
                total = 0.0f;
            }
        }

        if (content.voucher_id.isPresent()) {
            voucherService.usedVoucher(content.voucher_id.get());

            userService.updateDiscount(content.user_id, total * 0.15f);
        }

        LocalDate date = LocalDate.now();

        AppPurchase updated_purchase = purchaseService.updatePurchase(total, Date.valueOf(date), content.discount, user, purchase.getId());
        
        
        Integer previous = (int)(user.getTotal() / 100);
        Integer next = (int)((total + user.getTotal()) / 100);
        Integer count = next - previous;

        for (int i = 0; i < count; i++) {
            voucherService.createVoucher(user);
        }

        userService.updateTotal(content.user_id, total);

        return new ReturnPurchase(updated_purchase.getDate(), updated_purchase.getVoucher(), updated_purchase.getPrice(), content.products);
    }

    private boolean verifySignature(String signature, NewPurchase purchase, String public_key) throws Exception {
        Signature sign = Signature.getInstance("SHA256withRSA");

        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(public_key));
        PublicKey pubKey = kf.generatePublic(keySpec);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(purchase);
        oos.flush();

        sign.initVerify(pubKey);
        sign.update(baos.toByteArray());

        return sign.verify(Base64.getDecoder().decode(signature));
    }
}
