package io.hoon.realworld.api.service.comment;

import io.hoon.realworld.api.service.article.ArticleService;
import io.hoon.realworld.api.service.comment.response.CommentServiceResponse;
import io.hoon.realworld.api.service.profile.ProfileService;
import io.hoon.realworld.domain.article.Article;
import io.hoon.realworld.domain.article.comment.Comment;
import io.hoon.realworld.domain.article.comment.CommentRepository;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentService {

    private final ArticleService articleService;
    private final ProfileService profileService;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentServiceResponse createComment(AuthUser user, String slug, String body) {
        User author = user.toEntity();
        Article article = articleService.findBySlug(slug);

        Comment savedComment = commentRepository.save(Comment.builder()
                                                             .article(article)
                                                             .author(author)
                                                             .body(body)
                                                             .build());

        return CommentServiceResponse.of(savedComment, false);
    }

    public List<CommentServiceResponse> getComments(AuthUser user, String slug) {
        Article article = articleService.findBySlug(slug);
        List<Comment> comments = commentRepository.findByArticleIdOrderByCreatedAtDesc(article.getId()).get();

        if(user.isAnonymous()) {
            return comments.stream()
                           .map(comment -> CommentServiceResponse.of(comment, false))
                           .toList();
        }

        return comments.stream()
                       .map(comment -> CommentServiceResponse.of(comment, profileService.getFollow(user.getId(), comment.getAuthor().getUsername())
                                                                                        .isFollowing()))
                       .toList();
    }

    @Transactional
    public void deleteComment(AuthUser user, String slug, long commentId) {
        Article article = articleService.findBySlug(slug);
        commentRepository.findById(commentId).ifPresent(comment -> {
            if(comment.getArticle().getId() == article.getId() && comment.getAuthor().getId() == user.getId()) {
                commentRepository.delete(comment);
            }
        });
    }
}
