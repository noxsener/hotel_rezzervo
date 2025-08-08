package com.interestingdomain.gateway;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return (args) -> {
            System.out.println(generateJwt("AnonymousFreeBotUser"));
        };
    }

    public String generateJwt(String payload) throws InvalidKeyException, NoSuchAlgorithmException {
        String HEADER = "{\"alg\": \"HS256\", \"typ\": \"JWT\"}";
        String PART1 = Base64.getEncoder().encodeToString(HEADER.getBytes(StandardCharsets.UTF_8));
        String PART2 = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String PART1_PART2 = PART1 + "." + PART2;
        String PART3 = Base64.getEncoder().encodeToString(
                (doHMACSHA256(PART1_PART2)).getBytes(StandardCharsets.UTF_8));
        return PART1_PART2 + "." + PART3;
    }

    private String doHMACSHA256(String part1AndPart2)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec("7208ad6a531647939f610a2005649aa5".getBytes(), "HS256"));
        byte[] hashBytes = mac.doFinal(part1AndPart2.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }
}
