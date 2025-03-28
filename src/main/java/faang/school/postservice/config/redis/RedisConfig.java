package faang.school.postservice.config.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.dto.feed.FeedItemResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(
                redisProperties.host(),
                redisProperties.port());
        return new JedisConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        return template;
    }

    @Bean
    public RedisTemplate<String, FeedItemResponseDto> FeedItemRedisTemplate(RedisConnectionFactory connectionFactory,
                                                                            ObjectMapper redisObjectMapper) {
        RedisTemplate<String, FeedItemResponseDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper));
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper));

        return template;
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());

        return container;
    }

/*    @Bean
    public ObjectMapper redisObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }*/


    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Регистрируем модули для работы с Java 8 датами и Optional
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());

        // Отключаем запись дат как timestamp
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Настройка информации о типах для корректной десериализации
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(), // Используем стандартный валидатор
                ObjectMapper.DefaultTyping.EVERYTHING, // Включаем для всех типов
                JsonTypeInfo.As.PROPERTY // Сохраняем информацию о типе как свойство
        );

        return mapper;
    }

}