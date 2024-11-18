package io.hoon.realworld.domain.article;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findBySlug(String slug);

    int deleteBySlug(String slug);

    @Query("""
            SELECT a FROM Article a
            WHERE (:tag IS NULL OR :tag IN (SELECT t.tag.name FROM a.tagList t))
            AND (:author IS NULL OR a.author.username = :author)
            AND (:favorited IS NULL OR :favorited IN (SELECT fu.user.username FROM a.favoriteList fu))
            ORDER BY a.createdAt DESC
           """)
    Optional<List<Article>> findByArguments(@Param("tag") String tag,
                                            @Param("author") String author,
                                            @Param("favorited") String favorited,
                                            Pageable pageable);

    @Query("""
            SELECT a FROM Article a
            WHERE a.author.id IN :ids
            ORDER BY a.createdAt DESC
           """)
    Optional<List<Article>> findByAuthorIds(List<Long> ids, Pageable pageable);
}
