package com.example;


import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates a form to add a new ticket, 
 * including a combo box to assign the ticket to a selected user from /api/v1/auth/all.
 */
public class AddTicketWindow extends JFrame {

    // Endpoints
    private static final String USERS_ENDPOINT = "http://localhost:8080/api/v1/auth/all";
    private static final String TICKETS_ENDPOINT = "http://localhost:8080/api/v1/tickets";

    // Form fields
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityCombo;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> statusCombo;

    // Combo box for users
    private JComboBox<String> userCombo; 
    // We'll store user data (ID, username) in a parallel list or map
    private List<UserData> userList = new ArrayList<>();

    // Output area
    private JTextArea resultArea;

    public AddTicketWindow() {
        super("Add Ticket Form");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --------------------------------
        // 1) Form Panel
        // --------------------------------
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 8, 8));

        // Title
        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);

        // Description
        formPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 20);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll);

        // Priority
        formPanel.add(new JLabel("Priority:"));
        String[] priorities = { "LOW", "MEDIUM", "HIGH" };
        priorityCombo = new JComboBox<>(priorities);
        formPanel.add(priorityCombo);

        // Category
        formPanel.add(new JLabel("Category:"));
        String[] categories = { "NETWORK", "HARDWARE", "SOFTWARE", "OTHER" };
        categoryCombo = new JComboBox<>(categories);
        formPanel.add(categoryCombo);

        // Status (optional at creation)
        formPanel.add(new JLabel("Status:"));
        String[] statuses = { "New", "In_Progress", "Resolved" };
        statusCombo = new JComboBox<>(statuses);
        formPanel.add(statusCombo);

        // Assigned User Combo
        formPanel.add(new JLabel("Assign To User:"));
        userCombo = new JComboBox<>();
        formPanel.add(userCombo);

        // --------------------------------
        // 2) Button Panel
        // --------------------------------
        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit Ticket");
        submitButton.addActionListener(this::handleSubmitTicket);
        buttonPanel.add(submitButton);

        // --------------------------------
        // 3) Result Area
        // --------------------------------
        resultArea = new JTextArea(8, 50);
        resultArea.setEditable(false);
        JScrollPane resultScroll = new JScrollPane(resultArea);

        // --------------------------------
        // 4) Main Layout
        // --------------------------------
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(resultScroll, BorderLayout.SOUTH);
        add(mainPanel);

        // --------------------------------
        // 5) Load Users from /api/v1/auth/all
        // --------------------------------
        loadUsers();
    }

    /**
     * Loads the list of users from the backend and populates userCombo.
     */
    private void loadUsers() {
        String response = doGetRequest(USERS_ENDPOINT);

        // If there's an error
        if (response.startsWith("Error:")) {
            resultArea.setText(response);
            return;
        }

        try {
            // Parse JSON array
            JSONArray arr = new JSONArray(response);
            userList.clear(); // Make sure it's empty before adding
            userCombo.removeAllItems(); // Clear combo box

            for (int i = 0; i < arr.length(); i++) {
                JSONObject userJson = arr.getJSONObject(i);

                // Extract fields (adjust keys to match your actual JSON)
                String id = userJson.optString("id"); // e.g., "123e4567-e89b-12d3-a456-426614174000"
                String username = userJson.optString("username", "Unknown");

                // Create a small user object
                UserData user = new UserData(id, username);
                userList.add(user);

                // We'll show "username" in the combo
                userCombo.addItem(username);
            }

            // Optionally select the first item by default
            if (!userList.isEmpty()) {
                userCombo.setSelectedIndex(0);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            resultArea.setText("Failed to parse users: " + ex.getMessage());
        }
    }

    /**
     * Called when the user clicks "Submit Ticket".
     */
    private void handleSubmitTicket(ActionEvent event) {
        // 1) Gather form data
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priority = (String) priorityCombo.getSelectedItem();
        String category = (String) categoryCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();
        String assignedToId = null;

        // 2) Identify which user is selected in the combo
        int selectedIndex = userCombo.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < userList.size()) {
            UserData selectedUser = userList.get(selectedIndex);
            assignedToId = selectedUser.getId();  // This is the user’s ID/UUID
        }

        // 3) Build JSON for the ticket
        // Must match your TicketReq fields exactly
        // (title, description, priority, category, status, assignedTo_id)
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"title\":\"").append(escapeJson(title)).append("\",");
        sb.append("\"description\":\"").append(escapeJson(description)).append("\",");
        sb.append("\"priority\":\"").append(priority).append("\",");
        sb.append("\"category\":\"").append(category).append("\"");

        // If status is not blank, include it
        if (status != null && !status.isEmpty()) {
            sb.append(",\"status\":\"").append(status).append("\"");
        }

        // If assignedTo is not null or empty
        if (assignedToId != null && !assignedToId.isEmpty()) {
            sb.append(",\"assignedTo_id\":\"").append(assignedToId).append("\"");
        }

        sb.append("}");

        // 4) Post to backend
        String response = doPostRequest(TICKETS_ENDPOINT, sb.toString());
        resultArea.setText(response);
    }

    /**
     * Helper: GET request
     */
    private String doGetRequest(String urlString) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int status = connection.getResponseCode();
            InputStream stream = (status >= 200 && status < 300)
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error: " + ex.getMessage();
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignore) {}
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response.toString().trim();
    }

    /**
     * Helper: POST request with JSON body
     */
    private String doPostRequest(String urlString, String jsonBody) {
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

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = connection.getResponseCode();
            InputStream stream = (status >= 200 && status < 300)
                ? connection.getInputStream()
                : connection.getErrorStream();

            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error: " + ex.getMessage();
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignore) {}
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response.toString();
    }

    /**
     * Simple JSON escaping for quotes. For production, consider using a library
     * like Gson or Jackson for robust JSON building.
     */
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\"", "\\\"");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddTicketWindow window = new AddTicketWindow();
            window.setVisible(true);
        });
    }
}
