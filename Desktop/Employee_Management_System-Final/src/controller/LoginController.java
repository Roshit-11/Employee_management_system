package controller;

import model.AdminModel;
import view.login_form;
import view.register_form;
import view.homepage;
import view.homepage1;

import javax.swing.JOptionPane;

public class LoginController {

    private final login_form view;
    private final AdminModel model;
    
    public LoginController(login_form view, AdminModel model) {
        this.view = view;
        this.model = model;

        // Listen to login button
        this.view.addLoginListener(e -> handleLogin());

        // Listen to register button
        this.view.addRegisterListener(e -> openRegisterForm());
    }
    private void openRegister() {
        register_form regView = new register_form();
        new RegisterController(regView, model);
        regView.setVisible(true);
        view.dispose(); // optional
    }

    private void handleLogin() {
        view.clearError();

        String username = view.getUsername();
        String password = view.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            view.showError("Username and password are required.");
            return;
        }

        String role = model.authenticateUser(username, password);

        if (role == null) {
            view.showError("Invalid username or password.");
            return;
        }

        JOptionPane.showMessageDialog(null,
            "Login successful as " + role,
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );

        view.dispose(); // close login form

        if ("admin".equalsIgnoreCase(role)) {
            // Open admin homepage
            homepage home = new homepage();
            home.setUserGreeting(username);

            // Wire AddEmployeeController to homepage
            new AddEmployeeController(home, model);

            home.setVisible(true);

        } else if ("employee".equalsIgnoreCase(role)) {
            // You will create employee homepage later (optional)
            homepage1 empHome = new homepage1();
            empHome.setUserGreeting(username);
            empHome.setVisible(true);
            
        }
    }

    private void openRegisterForm() {
        view.dispose(); // close login form

        register_form reg = new register_form();
        new RegisterController(reg, model);
        reg.setVisible(true);
    }
    
}