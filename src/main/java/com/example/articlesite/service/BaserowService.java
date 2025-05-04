package com.example.articlesite.service;

import org.springframework.beans.factory.annotation.Value;

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

    public List<ArticleDto> getAllArticles() {
        System.out.println("\n=== STARTING BASEROW SERVICE ===");
        System.out.println("[CONFIG] API URL: " + (baserowApiUrl != null ? baserowApiUrl : "NOT SET"));
        System.out.println("[CONFIG] Token: " + (baserowToken != null ? "***" + baserowToken.substring(baserowToken.length() - 4) : "NOT SET"));

        if (baserowApiUrl == null || baserowToken == null) {
            System.err.println("[ERROR] Required configuration is missing!");
            return List.of();
        }

        List<ArticleDto> articles = new ArrayList<>();
        Request request = new Request.Builder()
                .url(baserowApiUrl)
                .header("Authorization", "Token " + baserowToken)
                .build();

        System.out.println("[REQUEST] Sending to: " + request.url());
        System.out.println("[REQUEST] Headers: " + request.headers());

        try (Response response = httpClient.newCall(request).execute()) {
            System.out.println("[RESPONSE] Code: " + response.code());
            System.out.println("[RESPONSE] Message: " + response.message());

            if (!response.isSuccessful()) {
                System.err.println("[ERROR] Request failed with code: " + response.code());
                if (response.body() != null) {
                    System.err.println("[ERROR] Error body: " + response.body().string());
                }
                return List.of();
            }

            if (response.body() == null) {
                System.err.println("[ERROR] Empty response body");
                return List.of();
            }

            String responseBody = response.body().string();
            System.out.println("[RESPONSE] Body length: " + responseBody.length());
            System.out.println("[RESPONSE] First 200 chars: " + responseBody.substring(0, Math.min(200, responseBody.length())));

            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray results = jsonResponse.getJSONArray("results");
            System.out.println("[DATA] Found " + results.length() + " articles");

            for (int i = 0; i < results.length(); i++) {
                JSONObject jsonObject = results.getJSONObject(i);
                ArticleDto article = new ArticleDto();

                article.setId(jsonObject.getLong("id"));
                article.setContent(jsonObject.getString("content"));
                article.setImageUrl(jsonObject.getString("image_url"));

                String dateStr = jsonObject.getString("date_created");
                article.setDateCreated(Instant.parse(dateStr));

                articles.add(article);
                System.out.println("[DATA] Added article ID: " + article.getId() +
                        ", Content length: " + article.getContent().length());
            }

            return articles;
        } catch (Exception e) {
            System.err.println("[CRITICAL] Exception in BaserowService:");
            e.printStackTrace();
            return List.of();
        } finally {
            System.out.println("=== BASEROW SERVICE FINISHED ===");
            System.out.println("Total articles fetched: " + articles.size() + "\n");
        }
    }

    public ArticleDto getArticleById(Long id) throws IOException {
        // Формируем URL для запроса конкретной статьи
        String apiUrl = baserowApiUrl.replaceAll("\\?.*$", "") + id + "/?user_field_names=true";
        System.out.println("[DEBUG] Fetching article from URL: " + apiUrl);

        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "Token " + baserowToken)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            System.out.println("[DEBUG] Response code: " + response.code());

            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch article. HTTP code: " + response.code());
            }

            if (response.body() == null) {
                throw new IOException("Empty response body");
            }

            String responseBody = response.body().string();
            System.out.println("[DEBUG] Response body: " + responseBody);

            JSONObject jsonObject = new JSONObject(responseBody);

            ArticleDto article = new ArticleDto();
            article.setId(jsonObject.getLong("id"));
            article.setContent(jsonObject.getString("content"));
            article.setImageUrl(jsonObject.getString("image_url"));
            article.setDateCreated(Instant.parse(jsonObject.getString("date_created")));

            return article;
        } catch (Exception e) {
            System.err.println("[ERROR] Error fetching article with ID " + id);
            e.printStackTrace();
            throw e;
        }
    }
}
