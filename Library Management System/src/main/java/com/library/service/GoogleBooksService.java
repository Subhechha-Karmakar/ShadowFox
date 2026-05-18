package com.library.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * ── TIER 2 UPGRADE ──────────────────────────────────────────────────────────
 *
 * Google Books API Integration
 *
 * Given an ISBN-10 or ISBN-13, fetches title and author automatically from:
 *   https://www.googleapis.com/books/v1/volumes?q=isbn:{ISBN}
 *
 * The response is JSON. We parse it with org.json (no extra dependencies).
 *
 * Example response path:
 *   items[0].volumeInfo.title
 *   items[0].volumeInfo.authors[0]
 *
 * ─────────────────────────────────────────────────────────────────────────────
 */
public class GoogleBooksService {

    private static final String API_BASE =
            "https://www.googleapis.com/books/v1/volumes?q=isbn:";

    /** Holds the result of a Google Books lookup. */
    public record BookInfo(String title, String author) {}

    /**
     * Fetches book info from the Google Books API for the given ISBN.
     * Uses java.net.HttpURLConnection — no extra HTTP library needed.
     *
     * @param isbn ISBN-10 or ISBN-13 (digits only, no hyphens)
     * @return BookInfo record, or null if not found / network error
     */
    public BookInfo fetchByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) return null;

        String urlString = API_BASE + isbn.trim();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5_000);   // 5 s connect timeout
            conn.setReadTimeout(8_000);      // 8 s read timeout
            conn.setRequestProperty("Accept", "application/json");

            int status = conn.getResponseCode();
            if (status != 200) {
                System.err.println("[GoogleBooks] HTTP " + status + " for ISBN " + isbn);
                return null;
            }

            // Read response body
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
            }
            conn.disconnect();

            // Parse JSON
            return parseResponse(sb.toString());

        } catch (IOException e) {
            System.err.println("[GoogleBooks] Network error: " + e.getMessage());
            return null;
        }
    }

    // ── JSON Parsing ──────────────────────────────────────────────────────────
    private BookInfo parseResponse(String json) {
        try {
            JSONObject root  = new JSONObject(json);
            int totalItems   = root.optInt("totalItems", 0);
            if (totalItems == 0) return null;

            JSONArray items  = root.optJSONArray("items");
            if (items == null || items.isEmpty()) return null;

            JSONObject volumeInfo = items.getJSONObject(0).optJSONObject("volumeInfo");
            if (volumeInfo == null) return null;

            String title  = volumeInfo.optString("title", "").trim();
            String author = "";

            JSONArray authors = volumeInfo.optJSONArray("authors");
            if (authors != null && !authors.isEmpty()) {
                author = authors.getString(0).trim();
            }

            return (title.isEmpty()) ? null : new BookInfo(title, author);

        } catch (Exception e) {
            System.err.println("[GoogleBooks] Parse error: " + e.getMessage());
            return null;
        }
    }
}
