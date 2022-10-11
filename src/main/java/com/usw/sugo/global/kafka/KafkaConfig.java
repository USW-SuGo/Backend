package com.usw.sugo.global.kafka;

import com.amazonaws.util.ImmutableMapParameter;
import com.usw.sugo.domain.majorchatting.chattingRoomMessaging.dto.ChattingMessage;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {
    //Sender Config
    @Bean
    public ProducerFactory<String, ChattingMessage> producerFactory(){
        return new DefaultKafkaProducerFactory<>(producerConfigs(),
                null, new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<String, ChattingMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    @Bean
    public Map<String, Object> producerConfigs(){

        return ImmutableMapParameter.<String, Object> builder()
                .put("bootstrap.servers", "localhost:9092") // 카프카 서버 ip , port 기본설정
                .put("key.serializer", IntegerSerializer.class)
                .put("value.serializer", JsonSerializer.class)
                .put("group.id", "spring-boot-test")
                .build();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChattingMessage> kafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, ChattingMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }


    @Bean
    public ConsumerFactory<String, ChattingMessage> consumerFactory(){
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), null,
                new JsonDeserializer<>(ChattingMessage.class));
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        return ImmutableMapParameter.<String, Object>builder()
                .put("bootstrap.servers", "localhost:9092")
                .put("key.deserializer", IntegerDeserializer.class)
                .put("value.deserializer", JsonDeserializer.class)
                .put("group.id", "spring-boot-test")
                .build();
    }

}