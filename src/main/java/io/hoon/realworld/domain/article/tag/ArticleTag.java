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
@Table(name = "article_tags")
@Entity
public class ArticleTag extends BaseEntity {

    @GeneratedValue(strategy = IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "article_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    @JoinColumn(name = "tag_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Tag tag;

    @Builder
    private ArticleTag(Article article, Tag tag) {
        this.article = article;
        this.tag = tag;
    }
}
