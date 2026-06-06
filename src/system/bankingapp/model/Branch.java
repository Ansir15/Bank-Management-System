package system.bankingapp.model;

import java.sql.Timestamp;

public class Branch {

    private int       branchId;
    private String    branchName;
    private String    branchCode;
    private String    city;
    private String    address;
    private String    phone;
    private Timestamp createdAt;

    public Branch(String branchName, String branchCode, String city,
                  String address, String phone) {
        this.branchName = branchName;
        this.branchCode = branchCode;
        this.city       = city;
        this.address    = address;
        this.phone      = phone;
    }

    public Branch(int branchId, String branchName, String branchCode, String city,
                  String address, String phone, Timestamp createdAt) {
        this.branchId   = branchId;
        this.branchName = branchName;
        this.branchCode = branchCode;
        this.city       = city;
        this.address    = address;
        this.phone      = phone;
        this.createdAt  = createdAt;
    }

    public int  getBranchId()
    {
        return branchId;
    }
    public String    getBranchName() { return branchName; }
    public String    getBranchCode() { return branchCode; }
    public String    getCity()       { return city; }
    public String    getAddress()    { return address; }
    public String    getPhone()      { return phone; }
    public Timestamp getCreatedAt()  { return createdAt; }

    public void setBranchName(String v) { branchName = v; }
    public void setBranchCode(String v) { branchCode = v; }
    public void setCity(String v)       { city       = v; }
    public void setAddress(String v)    { address    = v; }
    public void setPhone(String v)      { phone      = v; }
}