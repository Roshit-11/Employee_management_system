package model;

public class Employee {

    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String department;
    private String address;
    private String password;
    private String role;   // "employee"
    private String employeeType;
    
    public Employee(String fullName,
                String username,
                String email,
                String phone,
                String department,
                String address,
                String employeeType,
                String password,
                String role) {
    this.fullName     = fullName;
    this.username     = username;
    this.email        = email;
    this.phone        = phone;
    this.department   = department;
    this.address      = address;
    this.employeeType = employeeType;
    this.password     = password;
    this.role         = role;  // "employee"
}

    public String getFullName()   { return fullName; }
    public String getUsername()   { return username; }
    public String getEmail()      { return email; }
    public String getPhone()      { return phone; }
    public String getDepartment() { return department; }
    public String getAddress()    { return address; }
    public String getPassword()   { return password; }
    public String getRole()       { return role; }
    public String getEmployeeType() {
    return employeeType;
}
    
}