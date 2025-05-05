package com.example.articlesite.controller;

import com.example.articlesite.dto.ArticleDto;
import com.example.articlesite.service.BaserowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
@CrossOrigin(origins = {"https://mirai-tech.ru", "https://zen.yandex.ru"})
public class ArticleController {
    private final BaserowService baserowService;

    public ArticleController(BaserowService baserowService) {
        this.baserowService = baserowService;
    }

    @GetMapping
    public ResponseEntity<?> getAllArticles() {
        return ResponseEntity.ok(baserowService.getAllArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArticleById(@PathVariable Long id) {
        try {
            ArticleDto article = baserowService.getArticleById(id);
            return ResponseEntity.ok(article);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}