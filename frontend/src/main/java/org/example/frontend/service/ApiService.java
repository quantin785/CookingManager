package org.example.frontend.service;

import com.google.gson.reflect.TypeToken;
import org.example.frontend.config.ApiConfig;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

/**
 * Service HTTP générique.
 * Toutes les classes de service héritent de celle-ci pour mutualiser la logique HTTP.
 */
public abstract class ApiService {

    // ── Méthodes HTTP génériques ─────────────────────────────────────────────

    protected <T> T get(String url, Class<T> type) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = ApiConfig.HTTP_CLIENT
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return ApiConfig.GSON.fromJson(response.body(), type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected <T> List<T> getList(String url, Class<T> elementType) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = ApiConfig.HTTP_CLIENT
                    .send(request, HttpResponse.BodyHandlers.ofString());
            Type listType = TypeToken.getParameterized(List.class, elementType).getType();
            return ApiConfig.GSON.fromJson(response.body(), listType);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    protected <T> T post(String url, Object body, Class<T> type) {
        try {
            String json = ApiConfig.GSON.toJson(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = ApiConfig.HTTP_CLIENT
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return ApiConfig.GSON.fromJson(response.body(), type);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected <T> T put(String url, Object body, Class<T> type) {
        try {
            String json = ApiConfig.GSON.toJson(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = ApiConfig.HTTP_CLIENT
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return ApiConfig.GSON.fromJson(response.body(), type);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected boolean delete(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .DELETE()
                    .build();
            HttpResponse<String> response = ApiConfig.HTTP_CLIENT
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
