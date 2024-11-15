package io.hoon.realworld.domain.article;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.domain.article.favorite.Favorite;
import io.hoon.realworld.domain.article.favorite.FavoriteRepository;
import io.hoon.realworld.domain.article.tag.Tag;
import io.hoon.realworld.domain.article.tag.TagRepository;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Test
    @DisplayName("조건에 맞는 Article 목록을 조회한다.")
    @Transactional
    void findByArguments() {
        // Given
        User author = userRepository.save(User.builder().email("hoon@example.com").username("hoon").password("1234").build());
        User favoritedUser = userRepository.save(User.builder().email("emily@example.com").username("emily").password("1234").build());
        Tag tag1 = tagRepository.save(Tag.builder().name("Java").build());
        Tag tag2 = tagRepository.save(Tag.builder().name("Spring").build());
        Tag tag3 = tagRepository.save(Tag.builder().name("Python").build());
        Tag tag4 = tagRepository.save(Tag.builder().name("Flask").build());

        Article article1 = Article.builder().title("Java Basics").description("Intro to Java").body("Learn Java basics").build();
        article1.addAuthor(author);
        article1.addTag(tag1);
        article1.addTag(tag2);
        articleRepository.save(article1);

        Article article2 = Article.builder().title("Spring Boot Guide").description("Intro to Spring Boot").body("Learn Spring Boot").build();
        article2.addAuthor(author);
        article2.addTag(tag3);
        article2.addTag(tag4);
        articleRepository.save(article2);

        favoriteRepository.save(Favorite.create(article1, favoritedUser));

        // When
        Pageable pageable = PageRequest.of(0, 10);
        List<Article> articles = articleRepository.findByArguments(null, null, null, pageable).get();
        List<Article> articlesByTag = articleRepository.findByArguments("Java", null, null, pageable).get();
        List<Article> articlesByAuthor = articleRepository.findByArguments(null, "hoon", null, pageable).get();
        List<Article> articlesByFavorited = articleRepository.findByArguments(null, null, "emily", pageable).get();

        // Then
        assertThat(articlesByTag).hasSize(1);
        assertThat(articlesByTag.get(0)
                                .getTitle()).isEqualTo("Java Basics");

        assertThat(articlesByAuthor).hasSize(2);

        assertThat(articlesByFavorited).hasSize(1);
        assertThat(articlesByFavorited.get(0)
                                      .getTitle()).isEqualTo("Java Basics");
    }
}