package com.example.acme_backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.example.acme_backend.bodies.*;
import com.example.acme_backend.purchase.AppPurchase;
import com.example.acme_backend.voucher.AppVoucher;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AppUser> getUsers(){
        return userRepository.findAll();
    }

    public String newUser(NewUser user) throws Exception {
        UUID uuid = UUID.randomUUID();

        String hashed_password = hashPassword(user.password);

        AppUser createUser = new AppUser(user.name, user.username, hashed_password, user.public_key, user.card_number, uuid.toString(), 0.0f, 0.0f);

        userRepository.save(createUser);

        userRepository.flush();

        return uuid.toString();
    }

    public AppUser getByUuid(String uuid) {
        if (userRepository.findByUuid(uuid).isEmpty()) return null;
        return userRepository.findByUuid(uuid).get(0);
    }

    public AppUser getByUsername(String username) {
        if (userRepository.findByUsername(username).isEmpty()) return null;
        return userRepository.findByUsername(username).get(0);
    }

    public void updateDiscount(String uuid, float add_to_discount) {
        AppUser user = userRepository.findByUuid(uuid).get(0);

        user.addDiscount(add_to_discount);

        userRepository.save(user);

        userRepository.flush();
    }

    public void updateTotal(String uuid, Float add_to_total) {
        AppUser user = userRepository.findByUuid(uuid).get(0);

        user.addTotal(add_to_total);

        userRepository.save(user);

        userRepository.flush();
    }

    public void addVoucher(AppVoucher voucher, String uuid) {
        AppUser user = userRepository.findByUuid(uuid).get(0);

        user.addVoucher(voucher);

        userRepository.save(user);

        userRepository.flush();
    }

    public void addPurchase(String uuid, AppPurchase purchase) {
        AppUser user = userRepository.findByUuid(uuid).get(0);

        user.addPurchase(purchase);

        userRepository.save(user);

        userRepository.flush();
    }

    public void updatePassword(String uuid, String new_password) throws Exception {
        AppUser user = userRepository.findByUuid(uuid).get(0);

        String hashed_password = hashPassword(new_password);

        user.setPassword(hashed_password);

        userRepository.save(user);

        userRepository.flush();
    }

    public void updatePayment(String uuid, Long new_payment) {
        AppUser user = userRepository.findByUuid(uuid).get(0);

        user.setCard_number(new_payment);

        userRepository.save(user);

        userRepository.flush();
    }

    private String hashPassword(String password) throws Exception {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec keySpec = new PBEKeySpec(chars, salt, iterations, 64*8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = skf.generateSecret(keySpec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private byte[] getSalt() throws Exception {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        
        byte[] salt = new byte[16];

        random.nextBytes(salt);

        return salt;
    }

    private String toHex(byte[] array) {
        BigInteger big = new BigInteger(1, array);
        String hex = big.toString(16);

        int padding = (array.length * 2) - hex.length();

        if(padding > 0) {
            return String.format("%0" + padding + "d", 0) + hex;
        }
        else {
            return hex;
        }
    }
}