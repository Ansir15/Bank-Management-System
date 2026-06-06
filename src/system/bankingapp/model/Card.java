package system.bankingapp.model;

import java.sql.Timestamp;

public class Card {

    private int       cardId;
    private int       accountId;
    private String    accountNumber;
    private String    customerName;
    private String    cardNumber;
    private String    cardType;
    private String    expiryDate;
    private String    status;
    private Timestamp createdAt;

    public Card(int accountId, String cardNumber, String cardType, String expiryDate, String status) {
        this.accountId  = accountId;
        this.cardNumber = cardNumber;
        this.cardType   = cardType;
        this.expiryDate = expiryDate;
        this.status     = status;
    }

    public Card(int cardId, int accountId, String accountNumber, String customerName,
                String cardNumber, String cardType, String expiryDate, String status, Timestamp createdAt) {
        this.cardId        = cardId;
        this.accountId     = accountId;
        this.accountNumber = accountNumber;
        this.customerName  = customerName;
        this.cardNumber    = cardNumber;
        this.cardType      = cardType;
        this.expiryDate    = expiryDate;
        this.status        = status;
        this.createdAt     = createdAt;
    }

    public int       getCardId()        { return cardId; }
    public int       getAccountId()     { return accountId; }
    public String    getAccountNumber() { return accountNumber; }
    public String    getCustomerName()  { return customerName; }
    public String    getCardNumber()    { return cardNumber; }
    public String    getCardType()      { return cardType; }
    public String    getExpiryDate()    { return expiryDate; }
    public String    getStatus()        { return status; }
    public Timestamp getCreatedAt()     { return createdAt; }

    public void setAccountId(int v)     { accountId  = v; }
    public void setCardNumber(String v) { cardNumber  = v; }
    public void setCardType(String v)   { cardType    = v; }
    public void setExpiryDate(String v) { expiryDate  = v; }
    public void setStatus(String v)     { status      = v; }
}