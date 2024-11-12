package io.hoon.realworld;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// test 상에서 사용할 경우 자동으로 롤백된다. (비즈니스 로직 등 트랜잭션이 생성되어야 할 곳에 적용되어있지 않다면, 변경 감지 등 그런 작업이 누락될 수 있다.)
// 일반적으로 JpaRepository를 사용할 경우 내부적으로 @Transactional이 적용되어 있다.
//@Transactional

//JPA 관련한 빈만 주입
//리포지토리와 관련된 테스트를 수행할 때, 데이터베이스와 상호작용하는 코드에 집중하여 테스트할 수 있도록 설정
//@DataJpaTest

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTestSupport {

    @Autowired
    protected ObjectMapper objectMapper;
}
