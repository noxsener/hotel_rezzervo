package com.interestingdomain.hotelrezzervo.hotelservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic reservationInitTopic() {
        return new NewTopic("reservation.initiated", 1, (short) 1);
    }

    @Bean
    public NewTopic reservationSuccesfulTopic() {
        return new NewTopic("reservation.success", 1, (short) 1);
    }

    @Bean
    public NewTopic reservationFailTopic() {
        return new NewTopic("reservation.fail", 1, (short) 1);
    }
}
