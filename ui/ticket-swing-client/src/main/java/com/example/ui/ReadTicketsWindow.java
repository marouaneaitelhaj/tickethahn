package com.example.ui;

import com.example.entities.Ticket;
import com.example.service.TicketService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

public class ReadTicketsWindow extends JFrame {

    private TicketService ticketService = new TicketService();
    private TicketTableModel tableModel;
    private JTable ticketsTable;
    private JTextArea messageArea;

    // Search and filter components
    private JTextField searchField;
    private JComboBox<String> filterCombo;

    private List<Ticket> allTickets; // full list of tickets

    public ReadTicketsWindow() {
        super("View All Tickets");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadTickets();
    }

    private void initComponents() {
        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search by ID:"));
        searchField = new JTextField(10);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this::handleSearch);
        searchPanel.add(searchButton);
        searchPanel.add(new JLabel("Filter by Status:"));
        filterCombo = new JComboBox<>(new String[]{"All", "New", "In_Progress", "Resolved"});
        filterCombo.addActionListener(e -> handleFilter());
        searchPanel.add(filterCombo);
        JButton loadButton = new JButton("Load Tickets");
        loadButton.addActionListener(e -> loadTickets());
        searchPanel.add(loadButton);

        // Buttons panel
        JButton addTicketButton = new JButton("Add Ticket");
        addTicketButton.addActionListener(e -> {
            AddTicketWindow addWindow = new AddTicketWindow();
            addWindow.setVisible(true);
        });
        JButton editTicketButton = new JButton("Edit Ticket");
        editTicketButton.addActionListener(this::handleEditTicket);
        JButton updateStatusButton = new JButton("Update Status");
        updateStatusButton.addActionListener(this::handleUpdateStatus);
        JButton addCommentButton = new JButton("Add Comment");
        addCommentButton.addActionListener(this::handleAddComment);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addTicketButton);
        buttonPanel.add(editTicketButton);
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(addCommentButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Tickets table
        tableModel = new TicketTableModel(ticketService.getAllTickets());
        ticketsTable = new JTable(tableModel);
        String[] statusOptions = {"New", "In_Progress", "Resolved"};
        JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);
        ticketsTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusComboBox));
        JScrollPane tableScrollPane = new JScrollPane(ticketsTable);

        // Message area
        messageArea = new JTextArea(5, 70);
        messageArea.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(messageScrollPane, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void loadTickets() {
        allTickets = ticketService.getAllTickets();
        tableModel.setTickets(allTickets);
        messageArea.setText("Loaded " + allTickets.size() + " tickets.");
    }

    private void handleSearch(ActionEvent event) {
        String searchId = searchField.getText().trim();
        if (searchId.isEmpty()) {
            tableModel.setTickets(allTickets);
            messageArea.setText("Search field empty. Showing all tickets.");
            return;
        }
        List<Ticket> filtered = allTickets.stream()
                .filter(t -> t.getId().equalsIgnoreCase(searchId))
                .collect(Collectors.toList());
        tableModel.setTickets(filtered);
        messageArea.setText("Found " + filtered.size() + " ticket(s) with ID: " + searchId);
    }

    private void handleFilter() {
        String selectedStatus = (String) filterCombo.getSelectedItem();
        if ("All".equals(selectedStatus)) {
            tableModel.setTickets(allTickets);
            messageArea.setText("Showing all tickets.");
        } else {
            List<Ticket> filtered = allTickets.stream()
                    .filter(t -> t.getStatus().equalsIgnoreCase(selectedStatus))
                    .collect(Collectors.toList());
            tableModel.setTickets(filtered);
            messageArea.setText("Filtered by status: " + selectedStatus + " (" + filtered.size() + " found)");
        }
    }

    private void handleUpdateStatus(ActionEvent event) {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow < 0) {
            messageArea.setText("Please select a ticket to update its status.");
            return;
        }
        Ticket ticket = tableModel.getTicketAt(selectedRow);
        String newStatus = (String) ticketsTable.getValueAt(selectedRow, 4);
        boolean success = ticketService.updateTicketStatus(ticket.getId(), newStatus);
        if (success) {
            messageArea.setText("Ticket " + ticket.getId() + " updated to status: " + newStatus);
        } else {
            messageArea.setText("Failed to update status for ticket " + ticket.getId());
        }
    }

    private void handleAddComment(ActionEvent event) {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow < 0) {
            messageArea.setText("Please select a ticket to add a comment.");
            return;
        }
        Ticket ticket = tableModel.getTicketAt(selectedRow);
        String userId = JOptionPane.showInputDialog(this, "Enter your user id:");
        if (userId == null || userId.trim().isEmpty()) {
            messageArea.setText("User id is required to add a comment.");
            return;
        }
        String comment = JOptionPane.showInputDialog(this,
                "Enter your comment for ticket " + ticket.getId() + ":");
        if (comment != null && !comment.trim().isEmpty()) {
            boolean success = ticketService.addComment(ticket.getId(), userId.trim(), comment.trim());
            if (success) {
                messageArea.setText("Comment added for ticket " + ticket.getId());
            } else {
                messageArea.setText("Failed to add comment for ticket " + ticket.getId());
            }
        }
    }

    private void handleEditTicket(ActionEvent event) {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow < 0) {
            messageArea.setText("Please select a ticket to edit.");
            return;
        }
        Ticket ticket = tableModel.getTicketAt(selectedRow);
        EditTicketDialog editDialog = new EditTicketDialog(this, ticket);
        editDialog.setVisible(true);
        if (editDialog.isSaved()) {
            boolean success = ticketService.updateTicket(editDialog.getTicket());
            if (success) {
                messageArea.setText("Ticket updated successfully.");
                loadTickets();
            } else {
                messageArea.setText("Failed to update ticket.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ReadTicketsWindow window = new ReadTicketsWindow();
            window.setVisible(true);
        });
    }
}
