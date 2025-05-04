package com.example.articlesite.model;

import lombok.Data;
import java.time.Instant;

@Data
public class Article {
    private Long id;
    private String content;
    private String imageUrl;
    private Instant dateCreated;
}