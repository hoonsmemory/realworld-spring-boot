package io.hoon.realworld.domain.article;

import io.hoon.realworld.domain.BaseEntity;
import io.hoon.realworld.domain.article.favorite.Favorite;
import io.hoon.realworld.domain.article.tag.ArticleTag;
import io.hoon.realworld.domain.article.tag.Tag;
import io.hoon.realworld.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "articles")
@Entity
public class Article extends BaseEntity {

    @GeneratedValue(strategy = IDENTITY)
    @Id
    private Long id;

    @Column(length = 50, unique = true, nullable = false)
    private String slug;

    @Column(length = 50, unique = true, nullable = false)
    private String title;

    @Column(length = 50, nullable = false)
    private String description;

    @Column(length = 1000, nullable = false)
    private String body;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<ArticleTag> tagList = new ArrayList<>();

    @JoinColumn(name = "author_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favoriteList = new ArrayList<>();

    @Builder
    private Article(User author, String title, String description, String body) {
        this.author = author;
        this.slug = title.replaceAll(" ", "-");
        this.title = title;
        this.description = description;
        this.body = body;
    }

    public void addTag(@NotNull Tag tag) {
        ArticleTag articleTag = ArticleTag.builder()
                                          .article(this)
                                          .tag(tag)
                                          .build();

        if (this.tagList.stream().anyMatch(articleTag::equals)) {
            return;
        }

        this.tagList.add(articleTag);
    }

    public void updateTitle(String title) {
        this.title = title;
        this.slug = title.replaceAll(" ", "-");
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateBody(String body) {
        this.body = body;
    }

    public void addAuthor(@NotNull User author) {
        this.author = author;
    }

    public void favorite(Favorite favorite) {
        this.favoriteList.add(favorite);
        if (favorite.getArticle() != this) {
            favorite.setArticle(this);
        }
    }

    public void unfavorite(Favorite favorite) {
        this.favoriteList.remove(favorite);
    }

}
