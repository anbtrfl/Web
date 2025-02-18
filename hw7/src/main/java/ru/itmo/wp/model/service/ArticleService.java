package ru.itmo.wp.model.service;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.ArticleRepository;
import ru.itmo.wp.model.repository.impl.ArticleRepositoryImpl;
import ru.itmo.wp.web.exception.RedirectException;

import java.util.List;


public class ArticleService {
    private final ArticleRepository articleRepository = new ArticleRepositoryImpl();

    public void validateArticle(Article article) throws ValidationException {
        if (Strings.isNullOrEmpty(article.getText())) {
            throw new ValidationException("Text is empty");
        }
        if (article.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title is empty");
        }
        if (Strings.isNullOrEmpty(article.getTitle())) {
            throw new ValidationException("Title is empty");
        }
        if (article.getText().trim().isEmpty()) {
            throw new ValidationException("Text is required");
        }
        if (article.getTitle().length() > 255) {
            throw new ValidationException("Title is very long");
        }
    }

    public void save(Article article) {
        articleRepository.save(article);
    }

    public List<Article> findAll() {
        return articleRepository.findAll();
    }
}