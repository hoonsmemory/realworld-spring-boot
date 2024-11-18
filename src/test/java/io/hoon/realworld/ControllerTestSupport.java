package io.hoon.realworld;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hoon.realworld.api.controller.article.ArticleController;
import io.hoon.realworld.api.controller.user.UserController;
import io.hoon.realworld.api.service.article.ArticleService;
import io.hoon.realworld.api.service.comment.CommentService;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.config.AppConfig;
import io.hoon.realworld.domain.user.UserRepository;
import io.hoon.realworld.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        UserController.class,
        ArticleController.class
})
@Import({SecurityConfig.class, AppConfig.class})
// 컨트롤러 전용 테스트
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    @Qualifier("objectMapper")
    protected ObjectMapper objectMapper;

    @MockBean
    protected UserService userService;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected ArticleService articleService;

    @MockBean
    protected CommentService commentService;
}
