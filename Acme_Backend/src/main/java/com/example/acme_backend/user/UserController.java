package com.example.acme_backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

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
    public ResponseEntity<ReturnNewUser> newUser(@RequestBody NewUser user) throws Exception {

        String uuid = this.userService.newUser(user);

        File file = new File("src/main/resources/publickey.der");

        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        byte[] market_key = Files.readAllBytes(file.toPath());
        String encodeKey = Base64.getEncoder().encodeToString(market_key);

        ReturnNewUser new_user = new ReturnNewUser(uuid, encodeKey);

        return ResponseEntity.ok().body(new_user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Login login) throws Exception {
        AppUser user = this.userService.getByUsername(login.username);

        if (validatePassword(login.password, user.getPassword())) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/vouchers")
    public ResponseEntity<List<ReturnVoucher>> getVouchersUser(@RequestBody SignedId sign) throws Exception {
        AppUser user = userService.getByUuid(sign.uuid);

        if (!verifySignature(sign.signature, sign.uuid, user.getPublic_key())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Iterator<AppVoucher> vouchers = user.getVoucher().iterator();
        List<ReturnVoucher> retVouchers = new ArrayList<ReturnVoucher>();

        while (vouchers.hasNext()) {
            AppVoucher voucher = vouchers.next();
            retVouchers.add(new ReturnVoucher(voucher.getEmitted(), voucher.getUsed(), voucher.getDate(), user.getUsername(), voucher.getUuid()));
        }

        return ResponseEntity.ok().body(retVouchers);
    }

    @PostMapping("/purchases")
    public ResponseEntity<List<ReturnPurchase>> getPurchasesUser(@RequestBody SignedId sign) throws Exception {
        AppUser user = userService.getByUuid(sign.uuid);

        if (!verifySignature(sign.signature, sign.uuid, user.getPublic_key())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Iterator<AppPurchase> purchases = user.getPurchases().iterator();
        List<ReturnPurchase> retPurchases = new ArrayList<ReturnPurchase>();

        while (purchases.hasNext()) {
            AppPurchase purchase = purchases.next();
            Iterator<AppItem> items = purchase.getItems().iterator();

            List<ProductAndPrice> itemsList = new ArrayList<>();

            while(items.hasNext()) {
                AppItem item = items.next();
                AppProduct product = item.getProduct();
                itemsList.add(new ProductAndPrice(product.getName(), item.getQuantity() * product.getPrice()));
            }

            if (purchase.getVoucher() == null) {
                retPurchases.add(new ReturnPurchase(purchase.getDate(), purchase.getPrice(), itemsList));
            }
            else {
                retPurchases.add(new ReturnPurchase(purchase.getDate(), purchase.getPrice(), itemsList, purchase.getVoucher().getUuid()));
            }

        }

        return ResponseEntity.ok().body(retPurchases);
    }

    @PostMapping("/info")
    public ResponseEntity<ReturnUser> getUser(@RequestBody SignedId sign) throws Exception {
        AppUser user = userService.getByUuid(sign.uuid);

        if (!verifySignature(sign.signature, sign.uuid, user.getPublic_key())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        ReturnUser return_user = new ReturnUser(user.getName(), user.getUsername(), user.getDiscount(), user.getTotal());

        return ResponseEntity.ok().body(return_user);
    }

    @GetMapping("/uuid/{username}")
    public ResponseEntity<String> getUuid(@PathVariable("username") String username) {
        AppUser user = userService.getByUsername(username);

        return ResponseEntity.ok().body(user.getUuid());
    }

    @PostMapping("update/password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePassword sign) throws Exception {
        AppUser user = userService.getByUuid(sign.uuid);

        if (!verifySignature(sign.signature, sign.uuid, user.getPublic_key())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        userService.updatePassword(sign.uuid, sign.password);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("update/payment")
    public ResponseEntity<String> updatePayment(@RequestBody UpdatePayment sign) throws Exception {
        AppUser user = userService.getByUuid(sign.uuid);

        if (!verifySignature(sign.signature, sign.uuid, user.getPublic_key())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        userService.updatePayment(sign.uuid, sign.payment);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean verifySignature(String signature, String uuid, String public_key) throws Exception {
        Signature sign = Signature.getInstance("SHA256withRSA");

        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(public_key));
        PublicKey pubKey = kf.generatePublic(keySpec);

        sign.initVerify(pubKey);
        sign.update(uuid.getBytes());

        return sign.verify(Base64.getDecoder().decode(signature));
    }

    private boolean validatePassword(String verify, String hashed) throws Exception {
        String[] parts = hashed.split(":");
        int iterations = Integer.parseInt(parts[0]);

        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec keySpec = new PBEKeySpec(verify.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(keySpec).getEncoded();

        int diff = hash.length ^ testHash.length;
        
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }

        return diff == 0;
    }

    private byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }

        return bytes;
    }
}
