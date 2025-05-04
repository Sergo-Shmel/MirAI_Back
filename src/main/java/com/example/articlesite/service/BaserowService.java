package com.example.articlesite.service;

import com.example.articlesite.dto.ArticleDto;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BaserowService {
    private final OkHttpClient httpClient;

    @Value("${baserow.api.url:#{systemEnvironment['BASEROW_API_URL']}}")
    private String baserowApiUrl;

    @Value("${baserow.api.token:#{systemEnvironment['BASEROW_API_TOKEN']}}")
    private String baserowToken;

    public BaserowService(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public List<ArticleDto> getAllArticles() throws IOException {
        List<ArticleDto> articles = new ArrayList<>();

        Request request = new Request.Builder()
                .url(baserowApiUrl)
                .header("Authorization", "Token " + baserowToken)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                System.out.println("Raw response from Baserow:\n" + responseBody); // Логирование сырого ответа

                // Парсим JSON ответ
                JSONObject jsonResponse = new JSONObject(responseBody);
                JSONArray results = jsonResponse.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject jsonObject = results.getJSONObject(i);
                    ArticleDto article = new ArticleDto();

                    // Устанавливаем поля из JSON
                    article.setId(jsonObject.getLong("id"));
                    article.setContent(jsonObject.getString("content"));
                    article.setImageUrl(jsonObject.getString("image_url"));

                    // Парсим дату
                    String dateStr = jsonObject.getString("date_created");
                    Instant dateCreated = Instant.parse(dateStr);
                    article.setDateCreated(dateCreated);

                    articles.add(article);
                }
            }
        }
        return articles;
    }
}