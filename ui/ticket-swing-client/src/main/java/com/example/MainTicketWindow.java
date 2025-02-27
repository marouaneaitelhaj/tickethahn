package com.example;

import com.example.entities.CommentReq;
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
    private JTextArea commentArea;
    private JButton addCommentButton;
    private JPanel commentsPanel;
    private JScrollPane commentsScrollPane;
    private String authToken = null;
    private final ApiClient apiClient = ApiClient.getInstance();

    private static final String USERS_ENDPOINT = "http://localhost:8080/api/v1/user/all";
    private static final String TICKETS_ENDPOINT = "http://localhost:8080/api/v1/tickets";
    private static final String UPDATE_TICKET_ENDPOINT = "http://localhost:8080/api/v1/tickets/";
    private static final String COMMENTS_ENDPOINT = "http://localhost:8080/api/v1/comments";

    private boolean isEditing = false;
    private Ticket editingTicket = null;

    public MainTicketWindow() {
        super("Ticket Management System");
        setSize(1000, 800); // Increased height to accommodate comments section
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

        // Button Panel for Submitting, Editing, and Refreshing Tickets
        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit Ticket");
        submitButton.addActionListener((ActionEvent e) -> handleSubmitTicket());
        JButton editButton = new JButton("Edit Selected");
        editButton.addActionListener((ActionEvent e) -> handleEditTicket());
        JButton refreshButton = new JButton("Refresh Tickets");
        refreshButton.addActionListener((ActionEvent e) -> loadTickets());
        buttonPanel.add(submitButton);
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);

        gbc.gridy = 1;
        gbc.weighty = 0.1;
        add(buttonPanel, gbc);

        // Tickets Table
        tableModel = new TicketTableModel(ticketService.getAllTickets());
        ticketsTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(ticketsTable);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0.4;
        add(tableScroll, gbc);

        // Message Area for status and error messages
        messageArea = new JTextArea(5, 70);
        messageArea.setEditable(false);
        JScrollPane messageScroll = new JScrollPane(messageArea);

        gbc.gridy = 3;
        gbc.weighty = 0.1;
        add(messageScroll, gbc);

        // Comments Section
        initCommentsSection();
    }

    private void initCommentsSection() {
        commentsPanel = new JPanel();
        commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
        commentsPanel.setBorder(BorderFactory.createTitledBorder("Comments"));

        commentArea = new JTextArea(3, 20);
        addCommentButton = new JButton("Add Comment");
        addCommentButton.addActionListener((ActionEvent e) -> handleAddComment());

        JPanel commentInputPanel = new JPanel(new BorderLayout());
        commentInputPanel.add(new JScrollPane(commentArea), BorderLayout.CENTER);
        commentInputPanel.add(addCommentButton, BorderLayout.EAST);

        commentsScrollPane = new JScrollPane(commentsPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(commentInputPanel, gbc);

        gbc.gridy = 5;
        add(commentsScrollPane, gbc);
    }

    private void loadUsers() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                String response = apiClient.doGetRequest(USERS_ENDPOINT, true);
                if (response.startsWith("Error:")) {
                    messageArea.setText(response);
                    return null;
                }
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    userList.clear();
                    userCombo.removeAllItems();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject userObject = jsonArray.getJSONObject(i);
                        User user = new User();
                        user.setId(userObject.getString("id"));
                        user.setUsername(userObject.getString("username"));
                        userList.add(user);
                        userCombo.addItem(user.getUsername());
                    }
                    messageArea.setText("Users loaded successfully.");
                } catch (JSONException e) {
                    messageArea.setText("Failed to parse users: " + e.getMessage());
                }
                return null;
            }
        }.execute();
    }

    private void loadTickets() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                tableModel.setTickets(ticketService.getAllTickets());
                return null;
            }

            @Override
            protected void done() {
                ticketsTable.repaint();
                messageArea.setText("Tickets loaded successfully.");
            }
        }.execute();
    }

    private void handleSubmitTicket() {
        if (!validateForm()) {
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
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
                    return null;
                }

                String response;
                if (!isEditing) {
                    // Create a new ticket
                    response = apiClient.doPostRequest(TICKETS_ENDPOINT, ticketObject.toString(), true);
                } else {
                    // Update the existing ticket
                    try {
                        ticketObject.put("id", editingTicket.getId());
                    } catch (JSONException e) {
                        messageArea.setText("Failed to add ticket id: " + e.getMessage());
                        return null;
                    }
                    response = apiClient.doPutRequest(UPDATE_TICKET_ENDPOINT + editingTicket.getId(), ticketObject.toString(), true);
                    isEditing = false;
                    editingTicket = null;
                }
                messageArea.setText(response);
                clearForm();
                loadTickets();
                return null;
            }
        }.execute();
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

        // Load comments for the selected ticket
        loadComments(ticket);
    }

    private void handleAddComment() {
        String message = commentArea.getText().trim();
        if (message.isEmpty()) {
            messageArea.setText("Comment cannot be empty.");
            return;
        }

        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow < 0) {
            messageArea.setText("Please select a ticket to add a comment.");
            return;
        }

        Ticket selectedTicket = tableModel.getTicketAt(selectedRow);
        String ticketId = selectedTicket.getId();
        String userId = getCurrentUserId(); // Implement this method to get the current user's ID

        CommentReq commentReq = new CommentReq();
        commentReq.setTicket_id(ticketId);
        commentReq.setUser_id(userId);
        commentReq.setMessage(message);


        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                JSONObject commentObject = new JSONObject();
                try {
                    commentObject.put("ticket_id", commentReq.getTicket_id());
                    commentObject.put("user_id", commentReq.getUser_id());
                    commentObject.put("message", commentReq.getMessage());
                } catch (JSONException e) {
                    messageArea.setText("Failed to create comment JSON: " + e.getMessage());
                    return null;
                }
                String response = apiClient.doPostRequest(COMMENTS_ENDPOINT, commentObject.toString(), true);
                System.out.println(response);
                if (response.startsWith("Error:")) {
                    messageArea.setText(response);
                } else {
                    messageArea.setText("Comment added successfully.");
                    commentArea.setText("");
                    loadComments(selectedTicket);
                }
                return null;
            }
        }.execute();
    }

    private void loadComments(Ticket ticket) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                String response = apiClient.doGetRequest(COMMENTS_ENDPOINT + "?ticket_id=" + ticket.getId(), true);
                if (response.startsWith("Error:")) {
                    messageArea.setText(response);
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        commentsPanel.removeAll();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject commentObject = jsonArray.getJSONObject(i);
                            String commentText = commentObject.getString("message");
                            String createdAt = commentObject.optString("createdAt", "Unknown date");
                            JLabel commentLabel = new JLabel("<html><b>" + createdAt + ":</b> " + commentText + "</html>");
                            commentsPanel.add(commentLabel);
                        }
                        commentsPanel.revalidate();
                        commentsPanel.repaint();
                    } catch (JSONException e) {
                        messageArea.setText("Failed to parse comments: " + e.getMessage());
                    }
                }
                return null;
            }
        }.execute();
    }

    private String getCurrentUserId() {
        // Implement this method to return the current user's ID
        return apiClient.getCurrentUserId(); // Replace with actual logic
    }

    private boolean validateForm() {
        if (titleField.getText().trim().isEmpty()) {
            messageArea.setText("Title is required.");
            return false;
        }
        if (descriptionArea.getText().trim().isEmpty()) {
            messageArea.setText("Description is required.");
            return false;
        }
        return true;
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