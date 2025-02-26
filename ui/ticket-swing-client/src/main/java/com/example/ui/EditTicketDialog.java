package com.example.ui;

import javax.swing.*;
import com.example.entities.Ticket;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditTicketDialog extends JDialog {
    private Ticket ticket;
    private boolean saved = false;

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityCombo;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> statusCombo;

    public EditTicketDialog(Frame owner, Ticket ticket) {
        super(owner, "Edit Ticket", true);
        this.ticket = ticket;
        initComponents();
        populateFields();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 8, 8));

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

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this::handleSave);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFields() {
        titleField.setText(ticket.getTitle());
        descriptionArea.setText(ticket.getDescription() != null ? ticket.getDescription() : "");
        priorityCombo.setSelectedItem(ticket.getPriority());
        categoryCombo.setSelectedItem(ticket.getCategory());
        statusCombo.setSelectedItem(ticket.getStatus());
    }

    private void handleSave(ActionEvent event) {
        ticket.setTitle(titleField.getText().trim());
        ticket.setDescription(descriptionArea.getText().trim());
        ticket.setPriority((String) priorityCombo.getSelectedItem());
        ticket.setCategory((String) categoryCombo.getSelectedItem());
        ticket.setStatus((String) statusCombo.getSelectedItem());
        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Ticket getTicket() {
        return ticket;
    }
}
