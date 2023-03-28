package com.example.acme_backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import com.example.acme_backend.bodies.*;
import com.example.acme_backend.item.AppItem;
import com.example.acme_backend.product.AppProduct;
import com.example.acme_backend.purchase.AppPurchase;
import com.example.acme_backend.voucher.AppVoucher;

import java.io.*;

@RestController
@RequestMapping(path = "api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<AppUser> getUsers(){
        return userService.getUsers();
    }

    @PostMapping("/new")
    @ResponseBody
    public ReturnNewUser newUser(@RequestBody NewUser user) throws Exception {
        String uuid = this.userService.newUser(user);

        File file = new File("src/main/resources/publickey.pem");

        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        String market_key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());

        String send_key = market_key.replace("-----BEGIN PUBLIC KEY-----", "").replaceAll(System.lineSeparator(), "").replace("-----END PUBLIC KEY-----", "");

        return new ReturnNewUser(uuid, send_key);
    }

    @PostMapping("/vouchers")
    public List<ReturnVoucher> getVouchersUser(@RequestBody SignedId sign) {
        AppUser user = userService.getByUuid(sign.uuid);

        Iterator<AppVoucher> vouchers = user.getVoucher().iterator();
        List<ReturnVoucher> retVouchers = new ArrayList<ReturnVoucher>();

        while (vouchers.hasNext()) {
            AppVoucher voucher = vouchers.next();
            retVouchers.add(new ReturnVoucher(voucher.getEmitted(), voucher.getUsed(), voucher.getDate(), user.getUsername()));
        }

        return retVouchers;
    }

    @PostMapping("/purchases")
    public List<ReturnPurchase> getPurchasesUser(@RequestBody SignedId sign) {
        AppUser user = userService.getByUuid(sign.uuid);

        Iterator<AppPurchase> purchases = user.getPurchases().iterator();
        List<ReturnPurchase> retPurchases = new ArrayList<ReturnPurchase>();

        while (purchases.hasNext()) {
            AppPurchase purchase = purchases.next();
            Iterator<AppItem> items = purchase.getItems().iterator();

            List<ProductAndQuantity> itemsList = new ArrayList<ProductAndQuantity>();

            while(items.hasNext()) {
                AppItem item = items.next();
                AppProduct product = item.getProduct();
                itemsList.add(new ProductAndQuantity(product.getName(), item.getQuantity()));
            }

            retPurchases.add(new ReturnPurchase(purchase.getDate(), purchase.getVoucher(), purchase.getPrice(), itemsList));
        }

        return retPurchases;
    }
}
