package system.bankingapp.model;

import java.sql.Timestamp;

public class Customers {

    private int       customerId;
    private String    customerFullName;
    private String    email;
    private String    phone;
    private String    address;
    private String    cnic;
    private String    dateOfBirth;
    private String    gender;
    private Timestamp createdAt;

    public Customers(String customerFullName, String email, String phone,
                     String address, String cnic, String dateOfBirth, String gender) {
        this.customerFullName = customerFullName;
        this.email            = email;
        this.phone            = phone;
        this.address          = address;
        this.cnic             = cnic;
        this.dateOfBirth      = dateOfBirth;
        this.gender           = gender;
    }

    public Customers(int customerId, String customerFullName, String email, String phone,
                     String address, String cnic, String dateOfBirth, String gender, Timestamp createdAt) {
        this.customerId       = customerId;
        this.customerFullName = customerFullName;
        this.email            = email;
        this.phone            = phone;
        this.address          = address;
        this.cnic             = cnic;
        this.dateOfBirth      = dateOfBirth;
        this.gender           = gender;
        this.createdAt        = createdAt;
    }

    public int       getCustomerId()       { return customerId; }
    public String    getCustomerFullName() { return customerFullName; }
    public String    getEmail()            { return email; }
    public String    getPhone()            { return phone; }
    public String    getAddress()          { return address; }
    public String    getCnic()             { return cnic; }
    public String    getDateOfBirth()      { return dateOfBirth; }
    public String    getGender()           { return gender; }
    public Timestamp getCreatedAt()        { return createdAt; }

    public void setCustomerFullName(String n) { this.customerFullName = n; }
    public void setEmail(String e)            { this.email = e; }
    public void setPhone(String p)            { this.phone = p; }
    public void setAddress(String a)          { this.address = a; }
    public void setCnic(String c)             { this.cnic = c; }
    public void setDateOfBirth(String d)      { this.dateOfBirth = d; }
    public void setGender(String g)           { this.gender = g; }

    @Override
    public String toString() {
        return "Customer{id=" + customerId + ", name=" + customerFullName + ", email=" + email + "}";
    }
}