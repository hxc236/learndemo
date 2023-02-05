package com.lendemo.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String plaintext = "111111";
        String inputText = "123456";
        String ciphertext = passwordEncoder.encode(plaintext);
        boolean match1 = passwordEncoder.matches(inputText, ciphertext);
        System.out.println(ciphertext);

    }

}
