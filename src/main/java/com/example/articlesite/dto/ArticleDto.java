package com.example.articlesite.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class ArticleDto {
    private Long id;
    private String content;
    private String title; // Добавлено новое поле
    private String imageUrl;
    private Instant dateCreated;
}