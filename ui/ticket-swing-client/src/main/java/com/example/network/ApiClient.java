package com.example.network;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import lombok.Data;

@Data
public class ApiClient {

    private static ApiClient instance;
    private String token;

    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public ApiClient(String authToken) {
        if (authToken == null || authToken.isEmpty()) {
            throw new IllegalArgumentException("Auth token cannot be null or empty.");
        }
        this.token = authToken;
    }

    public ApiClient() {
    }

    public void setToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Auth token cannot be null or empty.");
        }
        this.token = token;
    }

    private void addAuthorizationHeader(HttpURLConnection connection) {
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Authorization token is missing. Please set a token before making authenticated requests.");
        }
        connection.setRequestProperty("Authorization", "Bearer " + token);
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        InputStream stream = (status >= 200 && status < 300)
                ? connection.getInputStream() : connection.getErrorStream();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString().trim();
        }
    }

    public String doGetRequest(String urlString, boolean withToken) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (withToken) {
                addAuthorizationHeader(connection);
            }

            return readResponse(connection);
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    public String doPostRequest(String urlString, String jsonBody, boolean withToken) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            if (withToken) {
                addAuthorizationHeader(connection);
            }

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            return readResponse(connection);
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    public String doPutRequest(String urlString, String jsonBody, boolean withToken) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            if (withToken) {
                addAuthorizationHeader(connection);
            }

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            return readResponse(connection);
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        } finally {
            if (connection != null) connection.disconnect();
        }
    }
}
