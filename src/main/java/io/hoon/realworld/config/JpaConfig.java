package io.hoon.realworld.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//JPA Auditing은 주로 데이터의 생성 및 수정과 관련된 메타데이터(예: 생성 일시, 수정 일시)를 자동으로 관리하는 데 사용
@EnableJpaAuditing
@Configuration
public class JpaConfig {
}