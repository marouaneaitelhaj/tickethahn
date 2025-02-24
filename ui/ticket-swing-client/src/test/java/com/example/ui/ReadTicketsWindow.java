package com.example.ui;

import com.example.entities.Ticket;
import com.example.service.TicketService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ReadTicketsWindow extends JFrame {

    private TicketService ticketService = new TicketService();
    private TicketTableModel tableModel;
    private JTable ticketsTable;
    private JTextArea messageArea;

    public ReadTicketsWindow() {
        super("View All Tickets");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        // Top panel with buttons
        JButton loadButton = new JButton("Load Tickets");
        loadButton.addActionListener(this::handleLoadTickets);

        JButton addTicketButton = new JButton("Add Ticket");
        addTicketButton.addActionListener(e -> {
            AddTicketWindow addWindow = new AddTicketWindow();
            addWindow.setVisible(true);
        });

        JButton updateStatusButton = new JButton("Update Status");
        updateStatusButton.addActionListener(this::handleUpdateStatus);

        JButton addCommentButton = new JButton("Add Comment");
        addCommentButton.addActionListener(this::handleAddComment);

        JPanel topPanel = new JPanel();
        topPanel.add(loadButton);
        topPanel.add(addTicketButton);
        topPanel.add(updateStatusButton);
        topPanel.add(addCommentButton);

        // Table for tickets
        tableModel = new TicketTableModel(ticketService.getAllTickets());
        ticketsTable = new JTable(tableModel);
        String[] statusOptions = { "New", "In_Progress", "Resolved" };
        JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);
        ticketsTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusComboBox));
        JScrollPane tableScrollPane = new JScrollPane(ticketsTable);

        // Message area for status/info messages
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

    private void handleLoadTickets(ActionEvent event) {
        List<Ticket> tickets = ticketService.getAllTickets();
        tableModel.setTickets(tickets);
        messageArea.setText("Loaded " + tickets.size() + " tickets.");
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

    // Updated to prompt for user id as well
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ReadTicketsWindow window = new ReadTicketsWindow();
            window.setVisible(true);
        });
    }
}
