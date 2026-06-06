package system.bankingapp.model;

import java.sql.Timestamp;

public class Employee {

    private int       employeeId;
    private String    fullName;
    private String    email;
    private String    phone;
    private String    role;
    private double    salary;
    private String    hireDate;
    private Timestamp createdAt;

    public Employee(String fullName, String email, String phone,
                    String role, double salary, String hireDate) {
        this.fullName = fullName;
        this.email    = email;
        this.phone    = phone;
        this.role     = role;
        this.salary   = salary;
        this.hireDate = hireDate;
    }

    public Employee(int employeeId, String fullName, String email, String phone,
                    String role, double salary, String hireDate, Timestamp createdAt) {
        this.employeeId = employeeId;
        this.fullName   = fullName;
        this.email      = email;
        this.phone      = phone;
        this.role       = role;
        this.salary     = salary;
        this.hireDate   = hireDate;
        this.createdAt  = createdAt;
    }

    public int       getEmployeeId() { return employeeId; }
    public String    getFullName()   { return fullName; }
    public String    getEmail()      { return email; }
    public String    getPhone()      { return phone; }
    public String    getRole()       { return role; }
    public double    getSalary()     { return salary; }
    public String    getHireDate()   { return hireDate; }
    public Timestamp getCreatedAt()  { return createdAt; }

    public void setFullName(String v) { fullName = v; }
    public void setEmail(String v)    { email    = v; }
    public void setPhone(String v)    { phone    = v; }
    public void setRole(String v)     { role     = v; }
    public void setSalary(double v)   { salary   = v; }
    public void setHireDate(String v) { hireDate = v; }
}