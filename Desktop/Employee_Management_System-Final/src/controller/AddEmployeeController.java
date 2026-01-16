package controller;

import model.AdminModel;
import model.Employee;
import view.homepage;

import javax.swing.JOptionPane;
import java.io.IOException;

public class AddEmployeeController {

    private final homepage view;
    private final AdminModel model;

    public AddEmployeeController(homepage view, AdminModel model) {
        this.view = view;
        this.model = model;

        // connect button in view to controller logic
        this.view.addEmployeeRegisterListener(e -> handleAddEmployee());
    }

    private void handleAddEmployee() {
        // Clear previous error message (if any)
        view.clearEmpError();

        // Read all fields from the view
        String fullName     = view.getEmpFullName();
        String username     = view.getEmpUsername();
        String email        = view.getEmpEmail();
        String phone        = view.getEmpPhone();
        String department   = view.getEmpDepartment();
        String address      = view.getEmpAddress();
        String password     = view.getEmpPassword();
        String employeeType = view.getEmpEmployeeType();   // <-- from combo box or similar

        // Basic validation
        if (fullName.isEmpty() || username.isEmpty() ||
            email.isEmpty() || phone.isEmpty() ||
            department.isEmpty() || address.isEmpty() ||
            password.isEmpty() || employeeType.isEmpty()) {

            view.showEmpError("All fields are required, including password and employee type.");
            return;
        }

        // BASIC: email format
        if (!isBasicEmail(email)) {
            view.showEmpError("Please enter a valid email address.");
            return;
        }

        // BASIC: phone digits only + 10 digits
        String phoneDigits = phone.trim();
        if (!phoneDigits.matches("\\d+")) {
            view.showEmpError("Phone number must contain digits only.");
            return;
        }
        if (phoneDigits.length() != 10) {
            view.showEmpError("Phone number must be exactly 10 digits.");
            return;
        }

        // BASIC: password length
        if (password.length() < 6) {
            view.showEmpError("Password must be at least 6 characters.");
            return;
        }

        // Check username uniqueness (admins + employees)
        if (model.isUserExists(username)) {
            view.showEmpError("Username already exists. Choose a different username.");
            return;
        }

        // role / status for employees
        String role = "employee";

        // Create Employee object (make sure Employee constructor matches this order)
        Employee emp = new Employee(
                fullName,
                username,
                email,
                phone,
                department,
                address,
                employeeType,
                password,
                role
        );

        try {
            model.saveEmployee(emp);
        } catch (IOException ex) {
            ex.printStackTrace();
            view.showEmpError("Error saving employee. Please try again.");
            return;
        }

        JOptionPane.showMessageDialog(
                null,
                "Employee registered successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Optional: clear the form after success (implement this in view if you want)
        // view.clearEmployeeForm();
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