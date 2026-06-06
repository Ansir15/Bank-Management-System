package system.bankingapp.model;

import java.sql.Timestamp;

public class Transaction {

    private int       transactionId;
    private int       accountId;
    private int       performedBy;
    private String    accountNumber;
    private String    performedByName;
    private String    transactionType;
    private double    amount;
    private String    description;
    private Timestamp transactionDate;

    public Transaction(int transactionId, int accountId, int performedBy,
                       String accountNumber, String performedByName,
                       String transactionType, double amount,
                       String description, Timestamp transactionDate) {
        this.transactionId   = transactionId;
        this.accountId       = accountId;
        this.performedBy     = performedBy;
        this.accountNumber   = accountNumber;
        this.performedByName = performedByName;
        this.transactionType = transactionType;
        this.amount          = amount;
        this.description     = description;
        this.transactionDate = transactionDate;
    }

    public Transaction(int accountId, int performedBy, String transactionType,
                       double amount, String description) {
        this.accountId       = accountId;
        this.performedBy     = performedBy;
        this.transactionType = transactionType;
        this.amount          = amount;
        this.description     = description;
    }

    public int       getTransactionId()   { return transactionId; }
    public int       getAccountId()       { return accountId; }
    public int       getPerformedBy()     { return performedBy; }
    public String    getAccountNumber()   { return accountNumber; }
    public String    getPerformedByName() { return performedByName; }
    public String    getTransactionType() { return transactionType; }
    public double    getAmount()          { return amount; }
    public String    getDescription()     { return description; }
    public Timestamp getTransactionDate() { return transactionDate; }
}