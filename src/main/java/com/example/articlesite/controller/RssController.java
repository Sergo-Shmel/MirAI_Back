package com.example.articlesite.controller;

import com.example.articlesite.dto.ArticleDto;
import com.example.articlesite.service.BaserowService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class RssController {

    private final BaserowService baserowService;
    private static final DateTimeFormatter RSS_DATE_FORMAT = DateTimeFormatter.RFC_1123_DATE_TIME;

    public RssController(BaserowService baserowService) {
        this.baserowService = baserowService;
    }

    @GetMapping(value = "/rss", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getRssFeed() {
        try {
            List<ArticleDto> articles = baserowService.getAllArticles();
            String rssFeed = generateRss(articles);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "https://mirai-tech.ru")
                    .header("Access-Control-Allow-Methods", "GET")
                    .body(rssFeed);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error generating RSS feed");
        }
    }

    private String generateRss(List<ArticleDto> articles) {
        StringBuilder rssBuilder = new StringBuilder();
        rssBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n")
                .append("  <channel>\n")
                .append("    <title>MirAI Tech Articles</title>\n")
                .append("    <link>https://mirai-tech.ru</link>\n")
                .append("    <description>Latest tech articles from MirAI</description>\n")
                .append("    <language>ru-ru</language>\n")
                .append("    <atom:link href=\"https://mirai-tech.ru/api/rss\" rel=\"self\" type=\"application/rss+xml\" />\n");

        articles.forEach(article -> {
            rssBuilder.append("    <item>\n")
                    .append("      <title>").append(escapeXml(article.getTitle())).append("</title>\n")
                    .append("      <link>https://mirai-tech.ru/article/").append(article.getId()).append("</link>\n")
                    .append("      <description>").append(escapeXml(article.getContent())).append("</description>\n")
                    .append("      <pubDate>").append(RSS_DATE_FORMAT.format(article.getDateCreated())).append("</pubDate>\n")
                    .append("      <guid isPermaLink=\"true\">https://mirai-tech.ru/article/").append(article.getId()).append("</guid>\n");

            if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
                rssBuilder.append("      <enclosure url=\"").append(article.getImageUrl()).append("\" length=\"0\" type=\"image/jpeg\" />\n");
            }

            rssBuilder.append("    </item>\n");
        });

        rssBuilder.append("  </channel>\n")
                .append("</rss>");

        return rssBuilder.toString();
    }

    private String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}