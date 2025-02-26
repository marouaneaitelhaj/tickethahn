package com.example.ui;

import javax.swing.*;

import com.example.MainTicketWindow;
import com.example.network.ApiClient;

import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginUI extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private MainTicketWindow parentWindow;

    public LoginUI(MainTicketWindow parent) {
        super(parent, "Login", true);
        this.parentWindow = parent;
        setSize(300, 200);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());
        initComponents();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this::handleLogin);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(loginButton, gbc);
    }

    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String response = new ApiClient().doPostRequest("http://localhost:8080/api/auth", 
                "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}");
        
        if (response.startsWith("Error:")) {
            JOptionPane.showMessageDialog(this, "Login failed!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            parentWindow.setAuthToken(response);
            dispose();
        }
    }
}
