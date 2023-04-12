package com.example.acme_backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
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

    public String newUser(NewUser user) {
        UUID uuid = UUID.randomUUID();

        AppUser createUser = new AppUser(user.name, user.username, user.password, user.public_key, user.card_number, uuid.toString(), 0.0f, 0.0f);

        userRepository.save(createUser);

        userRepository.flush();

        return uuid.toString();
    }

    public AppUser getByUuid(String uuid) {
        return userRepository.findByUuid(uuid).get(0);
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

    public void updatePassword(String uuid, String new_password) {
        AppUser user = userRepository.findByUuid(uuid).get(0);

        user.setPassword(new_password);

        userRepository.save(user);

        userRepository.flush();
    }

    public void updatePayment(String uuid, Long new_payment) {
        AppUser user = userRepository.findByUuid(uuid).get(0);

        user.setCard_number(new_payment);

        userRepository.save(user);

        userRepository.flush();
    }
}