package system.bankingapp.model;

import java.sql.Timestamp;

public class Account {

    private int       accountId;
    private int       customerId;
    private int       branchId;
    private String    customerName;
    private String    branchName;
    private String    accountNumber;
    private String    accountType;
    private double    balance;
    private String    status;
    private Timestamp createdAt;

    public Account(int accountId, int customerId, int branchId, String customerName,
                   String branchName, String accountNumber, String accountType,
                   double balance, String status, Timestamp createdAt) {
        this.accountId     = accountId;
        this.customerId    = customerId;
        this.branchId      = branchId;
        this.customerName  = customerName;
        this.branchName    = branchName;
        this.accountNumber = accountNumber;
        this.accountType   = accountType;
        this.balance       = balance;
        this.status        = status;
        this.createdAt     = createdAt;
    }

    public Account(int customerId, int branchId, String accountNumber,
                   String accountType, double balance, String status) {
        this.customerId    = customerId;
        this.branchId      = branchId;
        this.accountNumber = accountNumber;
        this.accountType   = accountType;
        this.balance       = balance;
        this.status        = status;
    }

    public int       getAccountId()     { return accountId; }
    public int       getCustomerId()    { return customerId; }
    public int       getBranchId()      { return branchId; }
    public String    getCustomerName()  { return customerName; }
    public String    getBranchName()    { return branchName; }
    public String    getAccountNumber() { return accountNumber; }
    public String    getAccountType()   { return accountType; }
    public double    getBalance()       { return balance; }
    public String    getStatus()        { return status; }
    public Timestamp getCreatedAt()     { return createdAt; }

    public void setCustomerId(int v)    { customerId    = v; }
    public void setBranchId(int v)      { branchId      = v; }
    public void setAccountNumber(String v) { accountNumber = v; }
    public void setAccountType(String v)   { accountType   = v; }
    public void setBalance(double v)    { balance  = v; }
    public void setStatus(String v)     { status   = v; }

    @Override
    public String toString() {
        return accountNumber + " | " + customerName + " | " + accountType + " | " + balance;
    }
}