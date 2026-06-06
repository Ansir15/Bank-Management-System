package system.bankingapp.model;

import java.sql.Timestamp;

public class Loan {

    private int       loanId;
    private int       customerId;
    private String    customerName;
    private String    loanType;
    private double    principalAmount;
    private double    interestRate;
    private int       tenureMonths;
    private String    status;
    private String    issuedDate;
    private Timestamp createdAt;

    public Loan(int customerId, String loanType, double principalAmount,
                double interestRate, int tenureMonths, String status, String issuedDate) {
        this.customerId      = customerId;
        this.loanType        = loanType;
        this.principalAmount = principalAmount;
        this.interestRate    = interestRate;
        this.tenureMonths    = tenureMonths;
        this.status          = status;
        this.issuedDate      = issuedDate;
    }

    public Loan(int loanId, int customerId, String customerName, String loanType,
                double principalAmount, double interestRate, int tenureMonths,
                String status, String issuedDate, Timestamp createdAt) {
        this.loanId          = loanId;
        this.customerId      = customerId;
        this.customerName    = customerName;
        this.loanType        = loanType;
        this.principalAmount = principalAmount;
        this.interestRate    = interestRate;
        this.tenureMonths    = tenureMonths;
        this.status          = status;
        this.issuedDate      = issuedDate;
        this.createdAt       = createdAt;
    }

    public int       getLoanId()          { return loanId; }
    public int       getCustomerId()      { return customerId; }
    public String    getCustomerName()    { return customerName; }
    public String    getLoanType()        { return loanType; }
    public double    getPrincipalAmount() { return principalAmount; }
    public double    getInterestRate()    { return interestRate; }
    public int       getTenureMonths()    { return tenureMonths; }
    public String    getStatus()          { return status; }
    public String    getIssuedDate()      { return issuedDate; }
    public Timestamp getCreatedAt()       { return createdAt; }

    public void setCustomerId(int v)       { customerId      = v; }
    public void setLoanType(String v)      { loanType        = v; }
    public void setPrincipalAmount(double v){ principalAmount = v; }
    public void setInterestRate(double v)  { interestRate    = v; }
    public void setTenureMonths(int v)     { tenureMonths    = v; }
    public void setStatus(String v)        { status          = v; }
    public void setIssuedDate(String v)    { issuedDate      = v; }

    public double getMonthlyInstallment() {
        double r = interestRate / 100.0 / 12.0;
        if (r == 0) return principalAmount / tenureMonths;
        return (principalAmount * r * Math.pow(1 + r, tenureMonths)) / (Math.pow(1 + r, tenureMonths) - 1);
    }

    public double getTotalPayable() {
        return getMonthlyInstallment() * tenureMonths;
    }
}