package com.example.articlesite.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class ArticleDto {
    private Long id;
    private String content;
    private String imageUrl;
    private Instant dateCreated;
}