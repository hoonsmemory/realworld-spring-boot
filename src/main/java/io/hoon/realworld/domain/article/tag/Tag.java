package io.hoon.realworld.domain.article.tag;

import io.hoon.realworld.domain.BaseEntity;
import io.hoon.realworld.domain.article.Article;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tags")
@Entity
public class Tag extends BaseEntity {

    @GeneratedValue(strategy = IDENTITY)
    @Id
    private Long id;

    @Column(length = 20, unique = true, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    @Builder
    private Tag(String name, Article article) {
        this.name = name;
        this.article = article;
    }

    public void addArticle(Article article) {
        this.article = article;
    }
}
