package io.hoon.realworld.domain.article;

import io.hoon.realworld.domain.BaseEntity;
import io.hoon.realworld.domain.article.tag.Tag;
import io.hoon.realworld.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
    private List<Tag> tagList = new ArrayList<>();

    @JoinColumn(name = "author_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    public static Article create(String title, String description, String body, List<String> tagList) {
        Article article = new Article();
        article.title = title;
        article.description = description;
        article.body = body;
        article.slug = title.replaceAll(" ", "-");

        if (tagList == null || tagList.size() == 0) {
            return article;
        }

        for (String tag : tagList) {
            Tag newTag = Tag.builder()
                            .name(tag)
                            .article(article)
                            .build();

            article.addTag(newTag);
        }

        return article;
    }

    public void addTag(Tag tag) {
        this.tagList.add(tag);
        if (tag.getArticle() != this) {
            tag.addArticle(this);
        }
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

    public void setAuthorId(User author) {
        this.author = author;
    }
}
