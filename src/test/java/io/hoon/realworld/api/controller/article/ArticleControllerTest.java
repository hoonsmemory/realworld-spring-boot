package io.hoon.realworld.api.controller.article;

import io.hoon.realworld.ControllerTestSupport;
import io.hoon.realworld.api.service.article.response.ArticleServiceResponse;
import io.hoon.realworld.api.service.comment.response.CommentServiceResponse;
import io.hoon.realworld.domain.article.Article;
import io.hoon.realworld.domain.article.comment.Comment;
import io.hoon.realworld.domain.article.tag.Tag;
import io.hoon.realworld.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ArticleControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("하나의 아티클을 조회한다.")
    void getArticle() throws Exception {
        // Given
        Article article = Article.builder().title("article subject").description("설명").body("내용").build();
        article.addAuthor(User.create("hoon@email.com", "hoon", "password"));
        article.addTag(Tag.builder().name("tag1").build());
        article.addTag(Tag.builder().name("tag2").build());
        when(articleService.getArticle(any(), any())).thenReturn(ArticleServiceResponse.of(article, false, false, 0));

        // When // Then
        mockMvc.perform(get("/api/articles/{slug}", "article-subject"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.article.slug").value("article-subject"))
               .andExpect(jsonPath("$.article.title").value("article subject"))
               .andExpect(jsonPath("$.article.description").value("설명"))
               .andExpect(jsonPath("$.article.body").value("내용"))
               .andExpect(jsonPath("$.article.tagList[0]").value("tag1"))
               .andExpect(jsonPath("$.article.tagList[1]").value("tag2"))
               .andExpect(jsonPath("$.article.favorited").value(false))
               .andExpect(jsonPath("$.article.favoritesCount").value(0))
               .andExpect(jsonPath("$.article.author.username").value("hoon"));
    }

    @Test
    @DisplayName("조건을 가진 아티클을 조회한다.")
    void getArticles() throws Exception {
        // Given
        Article article = Article.builder().title("article subject").description("설명").body("내용").build();
        article.addAuthor(User.create("hoon@email.com", "hoon", "password"));
        article.addTag(Tag.builder().name("tag1").build());
        article.addTag(Tag.builder().name("tag2").build());
        when(articleService.getArticles(any(), any())).thenReturn(List.of(
                ArticleServiceResponse.of(article, false, false, 0)
        ));

        // When // Then
        mockMvc.perform(get("/api/articles"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.articles[0].slug").value("article-subject"))
               .andExpect(jsonPath("$.articles[0].title").value("article subject"))
               .andExpect(jsonPath("$.articles[0].description").value("설명"))
               .andExpect(jsonPath("$.articles[0].body").value("내용"))
               .andExpect(jsonPath("$.articles[0].tagList[0]").value("tag1"))
               .andExpect(jsonPath("$.articles[0].tagList[1]").value("tag2"))
               .andExpect(jsonPath("$.articles[0].favorited").value(false))
               .andExpect(jsonPath("$.articles[0].favoritesCount").value(0))
               .andExpect(jsonPath("$.articles[0].author.username").value("hoon"));
    }

    @Test
    @DisplayName("아티클의 코멘트를 조회한다.")
    void getComments() throws Exception {
        // Given
        Article article = Article.builder().title("article subject").description("설명").body("내용").build();
        article.addAuthor(User.create("hoon@email.com", "hoon", "password"));
        article.addTag(Tag.builder().name("tag1").build());
        article.addTag(Tag.builder().name("tag2").build());

        User author = User.builder().email("hoon@email.com").username("hoon").bio("bio").image("https://image.img").build();

    	when(commentService.getComments(any(), any())).thenReturn(List.of(
                CommentServiceResponse.of(Comment.builder().id(1l).body("comment").article(article).author(author).build(), false)
        ));

        // When // Then
        mockMvc.perform(get("/api/articles/{slug}/comments", "article-subject"))
       .andDo(print())
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.comments[0].id").value(1l))
       .andExpect(jsonPath("$.comments[0].body").value("comment"))
       .andExpect(jsonPath("$.comments[0].author.username").value("hoon"))
       .andExpect(jsonPath("$.comments[0].author.bio").value("bio"))
       .andExpect(jsonPath("$.comments[0].author.image").value("https://image.img"));
    }
}