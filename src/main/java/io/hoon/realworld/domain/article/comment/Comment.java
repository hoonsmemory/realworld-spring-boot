package io.hoon.realworld.domain.article.comment;

import io.hoon.realworld.domain.BaseEntity;
import io.hoon.realworld.domain.article.Article;
import io.hoon.realworld.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
@Entity
public class Comment extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(length = 500, nullable = false)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Builder
    private Comment(long id, Article article, User author, String body) {
        this.id = id;
        this.article = article;
        this.author = author;
        this.body = body;
    }
}
