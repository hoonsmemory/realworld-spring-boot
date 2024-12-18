package io.hoon.realworld.domain.article.tag;

import io.hoon.realworld.domain.BaseEntity;
import io.hoon.realworld.domain.article.Article;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @Builder
    private Tag(String name) {
        this.name = name;
    }
}
