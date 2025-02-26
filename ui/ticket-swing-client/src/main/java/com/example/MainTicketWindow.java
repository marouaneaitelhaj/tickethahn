package com.example;

import com.example.entities.Ticket;
import com.example.entities.User;
import com.example.network.ApiClient;
import com.example.service.TicketService;
import com.example.ui.TicketTableModel;
import com.example.ui.LoginUI;
import javax.swing.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class MainTicketWindow extends JFrame {
    private TicketService ticketService = new TicketService();
    private List<User> userList = new ArrayList<>();
    private TicketTableModel tableModel;
    private JTable ticketsTable;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityCombo, categoryCombo, statusCombo, userCombo;
    private JTextArea messageArea;
    private String authToken = null;
    private final ApiClient apiClient = ApiClient.getInstance();

    private static final String USERS_ENDPOINT = "http://localhost:8080/api/v1/user/all";
    private static final String TICKETS_ENDPOINT = "http://localhost:8080/api/v1/tickets";
    // Hypothetical endpoint for updating tickets.
    private static final String UPDATE_TICKET_ENDPOINT = "http://localhost:8080/api/v1/tickets/";

    // Flag and reference to track if we are editing an existing ticket.
    private boolean isEditing = false;
    private Ticket editingTicket = null;

    public MainTicketWindow() {
        super("Ticket Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        checkAuthentication();
    }

    private void checkAuthentication() {
        if (authToken == null) {
            new LoginUI(this).setVisible(true);
        } else {
            initComponents();
            loadUsers();
            loadTickets();
        }
    }

    public void setAuthToken(String token) {
        this.authToken = token;
        apiClient.setToken(token);
        getContentPane().removeAll();
        initComponents();
        loadUsers();
        loadTickets();
        revalidate();
        repaint();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        // Form Panel for Adding/Editing Tickets
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Edit Ticket"));
        titleField = new JTextField();
        descriptionArea = new JTextArea(3, 20);
        priorityCombo = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        categoryCombo = new JComboBox<>(new String[]{"NETWORK", "HARDWARE", "SOFTWARE", "OTHER"});
        statusCombo = new JComboBox<>(new String[]{"New", "In_Progress", "Resolved"});
        userCombo = new JComboBox<>();

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descriptionArea));
        formPanel.add(new JLabel("Priority:"));
        formPanel.add(priorityCombo);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryCombo);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusCombo);
        formPanel.add(new JLabel("Assign To:"));
        formPanel.add(userCombo);

        // Button to clear the form (and cancel editing mode)
        JButton clearButton = new JButton("Clear Form");
        clearButton.addActionListener((ActionEvent e) -> clearForm());
        formPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 0.3;
        add(formPanel, gbc);

        // Button Panel for Submitting and Editing Tickets
        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit Ticket");
        submitButton.addActionListener((ActionEvent e) -> handleSubmitTicket());
        JButton editButton = new JButton("Edit Selected");
        editButton.addActionListener((ActionEvent e) -> handleEditTicket());
        buttonPanel.add(submitButton);
        buttonPanel.add(editButton);

        gbc.gridy = 1;
        gbc.weighty = 0.1;
        add(buttonPanel, gbc);

        // Tickets Table
        tableModel = new TicketTableModel(ticketService.getAllTickets());
        ticketsTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(ticketsTable);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0.6;
        add(tableScroll, gbc);

        // Message Area for status and error messages
        messageArea = new JTextArea(5, 70);
        messageArea.setEditable(false);
        JScrollPane messageScroll = new JScrollPane(messageArea);

        gbc.gridy = 3;
        gbc.weighty = 0.1;
        add(messageScroll, gbc);
    }

    private void loadUsers() {
        String response = apiClient.doGetRequest(USERS_ENDPOINT, true);
        if (response.startsWith("Error:")) {
            messageArea.setText(response);
            return;
        }
        userCombo.removeAllItems();
        userList.clear();

        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userObject = jsonArray.getJSONObject(i);
                User user = new User();
                user.setId(userObject.getString("id"));
                user.setUsername(userObject.getString("username"));
                // Set other fields as needed
                userList.add(user);
                userCombo.addItem(user.getUsername());
            }
            messageArea.setText("Users loaded successfully.");
        } catch (JSONException e) {
            messageArea.setText("Failed to parse users: " + e.getMessage());
        }
    }

    private void loadTickets() {
        tableModel.setTickets(ticketService.getAllTickets());
    }

    private void handleSubmitTicket() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priority = (String) priorityCombo.getSelectedItem();
        String category = (String) categoryCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();

        // Retrieve the assigned user's ID based on selection
        int selectedUserIndex = userCombo.getSelectedIndex();
        String assignedUserId = "";
        if (selectedUserIndex >= 0 && selectedUserIndex < userList.size()) {
            assignedUserId = userList.get(selectedUserIndex).getId();
        }

        JSONObject ticketObject = new JSONObject();
        try {
            ticketObject.put("title", title);
            ticketObject.put("description", description);
            ticketObject.put("priority", priority);
            ticketObject.put("category", category);
            ticketObject.put("status", status);
            ticketObject.put("assignedTo_id", assignedUserId);
        } catch (JSONException e) {
            messageArea.setText("Failed to create ticket JSON: " + e.getMessage());
            return;
        }

        String response;
        if (!isEditing) {
            // Create a new ticket
            response = apiClient.doPostRequest(TICKETS_ENDPOINT, ticketObject.toString(), true);
        } else {
            // Update the existing ticket. Ensure the ticket ID is included.
            try {
                ticketObject.put("id", editingTicket.getId());
            } catch (JSONException e) {
                messageArea.setText("Failed to add ticket id: " + e.getMessage());
                return;
            }
            response = apiClient.doPutRequest(UPDATE_TICKET_ENDPOINT + editingTicket.getId(), ticketObject.toString(), true);
            System.out.println("Ticket update response: " + response);
            isEditing = false;
            editingTicket = null;
        }
        messageArea.setText(response);
        clearForm();
        loadTickets();
    }

    private void handleEditTicket() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow < 0) {
            messageArea.setText("Please select a ticket to edit.");
            return;
        }
        Ticket ticket = tableModel.getTicketAt(selectedRow);
        editingTicket = ticket;
        isEditing = true;

        // Populate form fields with ticket data
        titleField.setText(ticket.getTitle());
        descriptionArea.setText(ticket.getDescription());
        priorityCombo.setSelectedItem(ticket.getPriority());
        categoryCombo.setSelectedItem(ticket.getCategory());
        statusCombo.setSelectedItem(ticket.getStatus());
        // Set assigned user if available
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getId().equals(ticket.getAssignedTo().getId())) {
                userCombo.setSelectedIndex(i);
                break;
            }
        }
        messageArea.setText("Editing ticket: " + ticket.getId());
    }

    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
        priorityCombo.setSelectedIndex(0);
        categoryCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        if (userCombo.getItemCount() > 0) {
            userCombo.setSelectedIndex(0);
        }
        isEditing = false;
        editingTicket = null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainTicketWindow().setVisible(true));
    }
}
