package com.example.ui;

import com.example.entities.User;
import com.example.network.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class AddTicketWindow extends JFrame {

    private static final String USERS_ENDPOINT   = "http://localhost:8080/api/v1/auth/all";
    private static final String TICKETS_ENDPOINT = "http://localhost:8080/api/v1/tickets";

    private final ApiClient apiClient = new ApiClient();

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityCombo;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> statusCombo;
    private JComboBox<String> userCombo;
    private List<User> userList = new ArrayList<>();
    private JTextArea resultArea;

    public AddTicketWindow() {
        super("Add Ticket Form");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        loadUsers();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 8, 8));

        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);

        formPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(descriptionArea));

        formPanel.add(new JLabel("Priority:"));
        priorityCombo = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        formPanel.add(priorityCombo);

        formPanel.add(new JLabel("Category:"));
        categoryCombo = new JComboBox<>(new String[]{"NETWORK", "HARDWARE", "SOFTWARE", "OTHER"});
        formPanel.add(categoryCombo);

        formPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"New", "In_Progress", "Resolved"});
        formPanel.add(statusCombo);

        formPanel.add(new JLabel("Assign To User:"));
        userCombo = new JComboBox<>();
        formPanel.add(userCombo);

        JButton submitButton = new JButton("Submit Ticket");
        submitButton.addActionListener(this::handleSubmitTicket);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);

        resultArea = new JTextArea(8, 50);
        resultArea.setEditable(false);
        JScrollPane resultScroll = new JScrollPane(resultArea);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(resultScroll, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void loadUsers() {
        String response = apiClient.doGetRequest(USERS_ENDPOINT);
        if (response.startsWith("Error:")) {
            resultArea.setText(response);
            return;
        }
        try {
            JSONArray arr = new JSONArray(response);
            userList.clear();
            userCombo.removeAllItems();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject userJson = arr.getJSONObject(i);
                String id = userJson.optString("id");
                String username = userJson.optString("username", "Unknown");
                userList.add(new User(id, username));
                userCombo.addItem(username);
            }
            if (!userList.isEmpty()) {
                userCombo.setSelectedIndex(0);
            }
        } catch (Exception ex) {
            resultArea.setText("Failed to parse users: " + ex.getMessage());
        }
    }

    private void handleSubmitTicket(ActionEvent e) {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priority = (String) priorityCombo.getSelectedItem();
        String category = (String) categoryCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();

        int selectedIndex = userCombo.getSelectedIndex();
        String assignedToId = null;
        if (selectedIndex >= 0 && selectedIndex < userList.size()) {
            assignedToId = userList.get(selectedIndex).getId();
        }

        String ticketJson = buildTicketJson(title, description, priority, category, status, assignedToId);
        String response = apiClient.doPostRequest(TICKETS_ENDPOINT, ticketJson);
        resultArea.setText(response);
    }

    private String buildTicketJson(
            String title,
            String description,
            String priority,
            String category,
            String status,
            String assignedToId
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"title\":\"").append(escapeJson(title)).append("\",");
        sb.append("\"description\":\"").append(escapeJson(description)).append("\",");
        sb.append("\"priority\":\"").append(priority).append("\",");
        sb.append("\"category\":\"").append(category).append("\"");
        if (status != null && !status.isEmpty()) {
            sb.append(",\"status\":\"").append(status).append("\"");
        }
        if (assignedToId != null && !assignedToId.isEmpty()) {
            sb.append(",\"assignedTo_id\":\"").append(assignedToId).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

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
