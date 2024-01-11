package org.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFrame extends JFrame {
    private final JTextField usernameTextField;
    private final JPasswordField passwordPasswordField;
    private final JButton loginButton;
    private final JButton registerButton;
    private final AirQualityMonitorApp parentApp;
    private final UserManager userManager;

    public LoginFrame(AirQualityMonitorApp parentApp, UserManager userManager) {
        this.parentApp = parentApp;
        this.userManager = userManager;

        setTitle("Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 250);

        setLocationRelativeTo(null);

        usernameTextField = new JTextField(20);
        passwordPasswordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Username:"), gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        add(usernameTextField, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        add(new JLabel("Password:"), gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        add(passwordPasswordField, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(loginButton, gbc);

        gbc.gridx++;
        add(registerButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameTextField.getText();
                char[] passwordChars = passwordPasswordField.getPassword();

                try {
                    if (isValidEmail(username) && userManager.authenticateUser(username, passwordChars)) {
                        parentApp.onLoginSuccess();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(LoginFrame.this, "Error connecting to the database", "Error", JOptionPane.ERROR_MESSAGE);
                }

                Arrays.fill(passwordChars, '0');
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameTextField.getText();
                char[] passwordChars = passwordPasswordField.getPassword();

                try {
                    if (isValidEmail(username) && userManager.registerUser(username, passwordChars)) {
                        parentApp.onLoginSuccess();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Invalid username or username already exists", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(LoginFrame.this, "Error connecting to the database", "Error", JOptionPane.ERROR_MESSAGE);
                }

                Arrays.fill(passwordChars, '0');
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
