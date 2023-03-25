package com.example.acme_backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import com.example.acme_backend.bodies.*;
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

}
