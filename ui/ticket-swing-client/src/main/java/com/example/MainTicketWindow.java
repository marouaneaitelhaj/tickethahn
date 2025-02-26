package com.example;

import com.example.entities.Ticket;
import com.example.entities.User;
import com.example.network.ApiClient;
import com.example.service.TicketService;
import com.example.ui.TicketTableModel;
import com.example.ui.LoginUI;

import javax.swing.*;
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
    private final ApiClient apiClient = new ApiClient();
    private static final String USERS_ENDPOINT = "http://localhost:8080/api/v1/auth/all";
    private static final String TICKETS_ENDPOINT = "http://localhost:8080/api/v1/tickets";
    private String authToken = null;

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

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
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
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 0.3;
        add(formPanel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit Ticket");
        submitButton.addActionListener(this::handleSubmitTicket);
        JButton editButton = new JButton("Edit Selected");
        editButton.addActionListener(this::handleEditTicket);
        buttonPanel.add(submitButton);
        buttonPanel.add(editButton);
        
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        add(buttonPanel, gbc);

        // Table
        tableModel = new TicketTableModel(ticketService.getAllTickets());
        ticketsTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(ticketsTable);
        
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0.6;
        add(tableScroll, gbc);

        // Message Area
        messageArea = new JTextArea(5, 70);
        messageArea.setEditable(false);
        JScrollPane messageScroll = new JScrollPane(messageArea);
        
        gbc.gridy = 3;
        gbc.weighty = 0.1;
        add(messageScroll, gbc);
    }

    private void loadUsers() {
        String response = apiClient.doGetRequest(USERS_ENDPOINT);
        if (response.startsWith("Error:")) {
            messageArea.setText(response);
            return;
        }
        userCombo.removeAllItems();
    }

    private void loadTickets() {
        tableModel.setTickets(ticketService.getAllTickets());
    }

    private void handleSubmitTicket(ActionEvent e) {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priority = (String) priorityCombo.getSelectedItem();
        String category = (String) categoryCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();
        String ticketJson = "{\"title\":\"" + title + "\", \"description\":\"" + description + "\"}";
        String response = apiClient.doPostRequest(TICKETS_ENDPOINT, ticketJson);
        messageArea.setText(response);
        loadTickets();
    }

    private void handleEditTicket(ActionEvent e) {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow < 0) {
            messageArea.setText("Please select a ticket to edit.");
            return;
        }
        Ticket ticket = tableModel.getTicketAt(selectedRow);
        titleField.setText(ticket.getTitle());
        descriptionArea.setText(ticket.getDescription());
        priorityCombo.setSelectedItem(ticket.getPriority());
        categoryCombo.setSelectedItem(ticket.getCategory());
        statusCombo.setSelectedItem(ticket.getStatus());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainTicketWindow().setVisible(true));
    }
}
