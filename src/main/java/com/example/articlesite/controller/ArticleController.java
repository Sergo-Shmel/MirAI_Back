package com.example.articlesite.controller;

import com.example.articlesite.dto.ArticleDto;
import com.example.articlesite.service.BaserowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*") // Добавляем CORS поддержку
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
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}