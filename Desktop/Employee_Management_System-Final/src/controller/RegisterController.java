package controller;

import model.Admin;
import model.AdminModel;
import view.register_form;
import view.homepage;  // make sure homepage is in package view

import javax.swing.JOptionPane;
import java.io.IOException;

public class RegisterController {

    private final register_form view;
    private final AdminModel model;

    public RegisterController(register_form view, AdminModel model) {
        this.view = view;
        this.model = model;

        this.view.addRegisterListener(e -> handleRegister());
    }

    private void handleRegister() {
        view.clearError();

        String name     = view.getNameField();
        String username = view.getUsername();
        String email    = view.getEmailField();
        String password = view.getPassword();

        // Basic validation
        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            view.showError("All fields are required.");
            return;
        }

        // BASIC: email format (very simple)
        if (!isBasicEmail(email)) {
            view.showError("Please enter a valid email address.");
            return;
        }

        // BASIC: password length
        if (password.length() < 6) {
            view.showError("Password must be at least 6 characters.");
            return;
        }

        // Check duplicate username (in file)
        if (model.isUserExists(username)) {
            view.showError("User already exists. Please choose another username.");
            return;
        }

        // For now plain password (later: hash here)
        String status = "admin"; // because this is admin registration

        Admin admin = new Admin(name, username, email, password, status);

        try {
           model.saveAdmin(admin);
        } catch (IOException ex) {
            ex.printStackTrace();
            view.showError("Error saving data. Please try again.");
            return;
        }

        JOptionPane.showMessageDialog(
                null,
                "Registration successful!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Open homepage and close register form
        homepage home = new homepage();
        new AddEmployeeController(home, model);
        home.setVisible(true);
        view.dispose();
    }

    // BASIC email validator: must contain '@' and a '.' after '@'
    private boolean isBasicEmail(String email) {
        if (email == null) return false;
        String e = email.trim();
        int at = e.indexOf('@');
        if (at <= 0 || at != e.lastIndexOf('@')) return false;
        int dot = e.indexOf('.', at + 1);
        return dot > at + 1 && dot < e.length() - 1;
    }
}