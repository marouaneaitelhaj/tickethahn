package com.example.network;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import lombok.Data;


@Data
public class ApiClient {

    private static ApiClient instance;


    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    private String token;

    public ApiClient(String authToken) {
        this.token = authToken;
    }

    public ApiClient() {
    }

    public void setToken(String token) {
        this.token = token;
    }

    private void addAuthorizationHeader(HttpURLConnection connection) {
        if (token != null && !token.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }
    }

    public String doGetRequest(String urlString, boolean withToken) {
        System.out.println("urlString = " + urlString + " token = " + token + " withToken = " + withToken);
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (withToken) {
                addAuthorizationHeader(connection);
            }
            int status = connection.getResponseCode();
            InputStream stream = (status >= 200 && status < 300)
                    ? connection.getInputStream() : connection.getErrorStream();
            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        } finally {
            try { if (reader != null) reader.close(); } catch (IOException ignore) {}
            if (connection != null) connection.disconnect();
        }
        return response.toString().trim();
    }

    public String doPostRequest(String urlString, String jsonBody, boolean withToken) {
        System.out.println("urlString = " + urlString + " token = " + token + " withToken = " + withToken);
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
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
            int status = connection.getResponseCode();
            InputStream stream = (status >= 200 && status < 300)
                    ? connection.getInputStream() : connection.getErrorStream();
            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        } finally {
            try { if (reader != null) reader.close(); } catch (IOException ignore) {}
            if (connection != null) connection.disconnect();
        }
        return response.toString();
    }

    public String doPutRequest(String urlString, String jsonBody, boolean withToken) {
        System.out.println("urlString = " + urlString + " token = " + token + " withToken = " + withToken);
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
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
            int status = connection.getResponseCode();
            InputStream stream = (status >= 200 && status < 300)
                    ? connection.getInputStream() : connection.getErrorStream();
            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        } finally {
            try { if (reader != null) reader.close(); } catch (IOException ignore) {}
            if (connection != null) connection.disconnect();
        }
        return response.toString();
    }
}
