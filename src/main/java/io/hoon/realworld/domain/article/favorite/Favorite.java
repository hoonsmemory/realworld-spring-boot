package io.hoon.realworld.domain.article.favorite;

import io.hoon.realworld.domain.BaseEntity;
import io.hoon.realworld.domain.article.Article;
import io.hoon.realworld.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "favorites")
@Entity
public class Favorite extends BaseEntity {

    @GeneratedValue(strategy = IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "article_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public static Favorite create(Article article, User user) {
        Favorite favorite = new Favorite();
        favorite.article = article;
        favorite.user = user;
        return favorite;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}
