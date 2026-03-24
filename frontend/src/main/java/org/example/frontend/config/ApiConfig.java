package org.example.frontend.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Configuration centralisée du client HTTP et de Gson.
 * Modifiez BASE_URL pour pointer vers votre backend.
 */
public class ApiConfig {

    public static final String BASE_URL = "http://localhost:8081/api";

    /** Client HTTP singleton (réutilisable et thread-safe) */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /** Instance Gson partagée pour la sérialisation/désérialisation JSON */
    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .create();
}
