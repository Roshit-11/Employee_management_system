/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author roshitlamichhane
 */
public class Admin {
        private String name;
    private String username;
    private String email;
    private String password;  // plain text for now (hash later)
    private String status;    // e.g. "admin" or "employee"

    public Admin(String name, String username, String email, String password, String status) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus() {
        return status;
    }
}
