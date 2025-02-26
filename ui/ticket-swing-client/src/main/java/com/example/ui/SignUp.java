package com.example.ui;

import javax.swing.*;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.MainTicketWindow;
import com.example.enums.Role;
import com.example.network.ApiClient;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SignUp extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JComboBox<Role> roleComboBox;
    private MainTicketWindow parentWindow;
    private ApiClient apiClient = ApiClient.getInstance();

    public SignUp(MainTicketWindow parent) {
        super(parent, "Sign Up", true);
        this.parentWindow = parent;
        setSize(300, 300);
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

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Role:"), gbc);
        roleComboBox = new JComboBox<>(Role.values());
        gbc.gridx = 1;
        add(roleComboBox, gbc);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(this::handleSignUp);
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(signUpButton, gbc);
    }

    private void handleSignUp(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String email = emailField.getText().trim();
        Role role = (Role) roleComboBox.getSelectedItem();
        String response = apiClient.doPostRequest("http://localhost:8080/api/auth/signup", 
                "{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"email\":\"" + email + "\",\"role\":\"" + role + "\"}", false);

        if (response.startsWith("Error:")) {
            JOptionPane.showMessageDialog(this, "Sign Up failed!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                String token = jsonResponse.getString("token");
                parentWindow.setAuthToken(token);
                apiClient.setToken(token);
            } catch (JSONException e) {
                JOptionPane.showMessageDialog(this, "Invalid response from server!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dispose();
        }
    }
}
