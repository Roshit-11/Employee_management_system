package model;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;   // use LinkedList for task storage

public class AdminModel {

    private static final String FILE_PATH = "./users.txt";

    private final List<Admin> admins = new ArrayList<>();
    private final List<Employee> employees = new ArrayList<>();

    public AdminModel() {
    }

    // --------------------------------------------------
    // COMMON: check if username exists (admin or employee)
    // --------------------------------------------------
    public boolean isUserExists(String username) {
        // in-memory check
        for (Admin a : admins) {
            if (a.getUsername().equalsIgnoreCase(username)) return true;
        }
        for (Employee e : employees) {
            if (e.getUsername().equalsIgnoreCase(username)) return true;
        }

        // file check
        File file = new File(FILE_PATH);
        if (!file.exists()) return false;

        String search = "\"username\":\"" + escape(username) + "\"";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(search)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    // --------------------------------------------------
    // ADMIN saving
    // --------------------------------------------------
    public void saveAdmin(Admin admin) throws IOException {
        admins.add(admin);
        String json = toJsonAdmin(admin);
        writeLineToFile(json);
    }

    private String toJsonAdmin(Admin admin) {
        return "{"
                + "\"role\":\"admin\","
                + "\"name\":\""      + escape(admin.getName())     + "\","
                + "\"username\":\""  + escape(admin.getUsername()) + "\","
                + "\"email\":\""     + escape(admin.getEmail())    + "\","
                + "\"password\":\""  + escape(admin.getPassword()) + "\""
                + "}";
    }

    // --------------------------------------------------
    // EMPLOYEE saving
    // --------------------------------------------------
    public void saveEmployee(Employee emp) throws IOException {
        employees.add(emp);
        String json = toJsonEmployee(emp);
        writeLineToFile(json);
    }
    
    public static int getTotalEmployeeCount() {
    File file = new File(FILE_PATH);
    if (!file.exists()) return 0;

    int count = 0;

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("\"role\":\"employee\"")) {
                count++;
            }
        }
    } catch (IOException ex) {
        ex.printStackTrace();
    }

    return count;
}
    private String toJsonEmployee(Employee emp) {
        return "{"
                + "\"role\":\"employee\","
                + "\"fullName\":\""     + escape(emp.getFullName())    + "\","
                + "\"username\":\""     + escape(emp.getUsername())    + "\","
                + "\"email\":\""        + escape(emp.getEmail())       + "\","
                + "\"phone\":\""        + escape(emp.getPhone())       + "\","
                + "\"department\":\""   + escape(emp.getDepartment())  + "\","
                + "\"employeeType\":\"" + escape(emp.getEmployeeType())+ "\","
                + "\"address\":\""      + escape(emp.getAddress())     + "\","
                + "\"password\":\""     + escape(emp.getPassword())    + "\""
                + "}";
    }

    // --------------------------------------------------
    // Common helpers
    // --------------------------------------------------
    private void writeLineToFile(String json) throws IOException {
        File file = new File(FILE_PATH);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(json);
            bw.newLine();
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
    }

    public List<Admin> getAdmins() {
        return admins;
    }

    public List<Employee> getEmployees() {
        return employees;
    }
    public String authenticateUser(String username, String password) {
    File file = new File(FILE_PATH);

    if (!file.exists()) {
        return null;
    }

    String userPattern = "\"username\":\"" + escape(username) + "\"";
    String passPattern = "\"password\":\"" + escape(password) + "\"";

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = br.readLine()) != null) {

            // Does line contain matching username + password?
            if (line.contains(userPattern) && line.contains(passPattern)) {

                // Determine role
                if (line.contains("\"role\":\"admin\"")) {
                    return "admin";
                } 
                if (line.contains("\"role\":\"employee\"")) {
                    return "employee";
                }

                return "unknown";
            }
        }
    } catch (IOException ex) {
        ex.printStackTrace();
    }

    return null; // no match
    }
// In-memory tasks & weekly remarks per employee (shared across app)

    // use LinkedList internally for tasks
    private static final Map<String, LinkedList<String>> TASKS_BY_USER = new HashMap<>();
    private static final Map<String, String> REMARKS_BY_USER = new HashMap<>();
    private static final Map<String, Integer> RATING_BY_USER = new HashMap<>();

    /**
     * Save (overwrite) tasks + weekly remarks for a given username.
     * This is purely in-memory and shared across all AdminModel instances.
     */
    public static synchronized void saveTasksAndRemarks(String username, List<String> tasks, String remarks) {
        if (username == null || username.trim().isEmpty()) {
            return;
        }
        String key = username.trim().toLowerCase();

        // Normalize tasks list (defensive copy, trim, drop empties)
        LinkedList<String> copy = new LinkedList<>();
        if (tasks != null) {
            for (String t : tasks) {
                if (t == null) continue;
                String tt = t.trim();
                if (!tt.isEmpty()) copy.add(tt);
            }
        }
        TASKS_BY_USER.put(key, copy);

        String r = (remarks == null) ? "" : remarks.trim();
        REMARKS_BY_USER.put(key, r);
    }
    /**
     * Get a copy of tasks for a username; never returns null.
     */
    public static synchronized List<String> getTasks(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String key = username.trim().toLowerCase();
        LinkedList<String> stored = TASKS_BY_USER.get(key);
        if (stored == null) {
            return Collections.emptyList();
        }
        // Return a safe copy as List
        return new ArrayList<>(stored);
    }
    /**
     * Get weekly remarks for a username; never returns null (may be empty).
     */
    public static synchronized String getWeeklyRemarks(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "";
        }
        String key = username.trim().toLowerCase();
        String r = REMARKS_BY_USER.get(key);
        return (r == null) ? "" : r;
    }
    
public static synchronized int getWeeklyRating(String username) {
    if (username == null || username.trim().isEmpty()) return -1;

    String key = username.trim().toLowerCase();
    Integer v = RATING_BY_USER.get(key);
    return (v == null) ? -1 : v;
}

// ==============================
// Weekly rating (0-10) per employee (shared in-memory)
// ==============================
public static synchronized void saveWeeklyRating(String username, int rating) {
    if (username == null || username.trim().isEmpty()) return;
    if (rating < 0 || rating > 10) return;

    String key = username.trim().toLowerCase();
    RATING_BY_USER.put(key, rating);
}



// ==========================================================
// EMPLOYEE LIST FOR DISPLAY/SORT/SEARCH (read from users.txt)
// ==========================================================

/**
 * Reads `users.txt` and returns a LinkedList containing only users with role=employee.
 * Intended for Admin "Display Employees" screen (sort/search can be done on this list).
 *
 * This does NOT change your existing save logic; it only reads the file.
 */
public static synchronized LinkedList<Employee> loadEmployeesFromFileAsLinkedList() {
    LinkedList<Employee> result = new LinkedList<>();

    File file = new File(FILE_PATH);
    if (!file.exists()) return result;

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.contains("\"role\":\"employee\"")) continue;

            String fullName = readJsonValue(line, "fullName");
            if (fullName == null || fullName.isBlank()) {
                // fallback for older records (if any)
                fullName = readJsonValue(line, "name");
            }

            String username = readJsonValue(line, "username");
            String email = readJsonValue(line, "email");
            String phone = readJsonValue(line, "phone");
            String department = readJsonValue(line, "department");
            String employeeType = readJsonValue(line, "employeeType");
            String address = readJsonValue(line, "address");
            String password = readJsonValue(line, "password");

            // Avoid nulls
            if (fullName == null) fullName = "";
            if (username == null) username = "";
            if (email == null) email = "";
            if (phone == null) phone = "";
            if (department == null) department = "";
            if (employeeType == null) employeeType = "";
            if (address == null) address = "";
            if (password == null) password = "";

            // Match your Employee constructor signature used elsewhere in this project.
            // (This matches the same ordering used in saveEmployee/toJsonEmployee.)
            Employee emp = new Employee(
                    fullName,
                    username,
                    email,
                    phone,
                    department,
                    address,
                    employeeType,
                    password,
                    "employee"
            );

            result.add(emp);
        }
    } catch (IOException ex) {
        ex.printStackTrace();
    }

    return result;
}

/**
 * Lightweight JSON value reader for the one-line-per-user format you store in users.txt.
 * NOTE: This is not a general JSON parser; it is sufficient for your current file format.
 */
private static String readJsonValue(String jsonLine, String key) {
    if (jsonLine == null || key == null) return null;

    String needle = "\"" + key + "\":\"";
    int start = jsonLine.indexOf(needle);
    if (start < 0) return null;

    start += needle.length();
    int end = jsonLine.indexOf('"', start);
    if (end < 0) return null;

    String raw = jsonLine.substring(start, end);
    return raw.replace("\\\"", "\"");
}
}