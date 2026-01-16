/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.LoginController;
import model.AdminModel;
import view.login_form;

/**
 *
 * @author roshitlamichhane
 */
public class MainApp {
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {
            // Create shared model
            AdminModel model = new AdminModel();

            // Create login view
            login_form view = new login_form();

            // Wire controller (handles Login + Register button on login screen)
            new LoginController(view, model);

            // Show login form
            view.setVisible(true);
        });
    }
}