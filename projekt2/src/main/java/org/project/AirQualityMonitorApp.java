package org.project;

import org.apache.log4j.BasicConfigurator;

import javax.swing.*;
import java.sql.SQLException;

public class AirQualityMonitorApp {
    private AirQualityMonitorFrame airQualityMonitorFrame;
    private final UserManager userManager;
    public AirQualityMonitorApp() throws SQLException {
        // Set up log4j configuration
        // (You can also configure log4j through an external configuration file)
        BasicConfigurator.configure();

        // Inicjalizacja użytkowników
        userManager = new UserManager();

        // Sprawdź, czy użytkownik jest zalogowany
        if (userManager.isUserLoggedIn()) {
            initApp();
        } else {
            showLoginScreen();
        }

    }
    private void initApp() {
        SwingUtilities.invokeLater(() -> {
            airQualityMonitorFrame = new AirQualityMonitorFrame(this);
        });
    }

    private void showLoginScreen() {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(this, userManager);
            loginFrame.setVisible(true);
        });
    }

    public void onLoginSuccess() {
        initApp();
    }
    public void run() {
        // Your main program logic can go here
        System.out.println("Application is running.");
    }
}
