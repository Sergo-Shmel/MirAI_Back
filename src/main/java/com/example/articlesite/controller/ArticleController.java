package com.example.articlesite.controller;

import com.example.articlesite.dto.ArticleDto;
import com.example.articlesite.service.BaserowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final BaserowService baserowService;

    public ArticleController(BaserowService baserowService) {
        this.baserowService = baserowService;
    }

    @GetMapping
    public List<ArticleDto> getAllArticles() throws IOException {
        return baserowService.getAllArticles();
    }
    @GetMapping("/articles/{id}")
    public ResponseEntity<ArticleDto> getArticleById(@PathVariable Long id) throws IOException {
        ArticleDto article = baserowService.getArticleById(id);
        return ResponseEntity.ok(article);
    }
}