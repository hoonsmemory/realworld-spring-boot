package io.hoon.realworld.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.modules(iso8601SerializeModule()) // ISO 8601 날짜 직렬화를 위한 커스텀 모듈 추가
                      .featuresToEnable(DeserializationFeature.UNWRAP_ROOT_VALUE) // 역직렬화 시 루트 값 언랩핑 활성화
                      .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS) // 직렬화 시 빈 빈즈 실패 비활성화
                      .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // 날짜를 타임스탬프로 쓰기 비활성화
                      .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // 역직렬화 시 알 수 없는 속성 실패 비활성화
                      .build();
    }

    private Module iso8601SerializeModule() {
        return new JavaTimeModule().addSerializer(LocalDateTime.class, new JsonSerializer<>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                String formattedDateTime = value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                gen.writeString(formattedDateTime);
            }
        });
    }
}
