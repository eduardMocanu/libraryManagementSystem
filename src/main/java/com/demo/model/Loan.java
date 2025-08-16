package com.demo.model;
import java.time.*;

public class Loan {
    private final LocalDate loanStart;
    private final LocalDate loanEnd;
    private final String clientId;
    private final String bookISBN;
    private final String id;
    private boolean active;
    private boolean emailed;

    public Loan(String id, LocalDate loanStart, LocalDate loanEnd, String clientId, String bookISBN, boolean active, boolean emailed){
        this.loanStart = loanStart;
        this.loanEnd = loanEnd;
        this.clientId = clientId;
        this.bookISBN = bookISBN;
        this.id = id;
        this.active = active;
        this.emailed = emailed;
    }

    //GETTERS
    public String getBookISBN() {
        return bookISBN;
    }

    public String getClientId() {
        return clientId;
    }

    public LocalDate getLoanEnd() {
        return loanEnd;
    }

    public LocalDate getLoanStart() {
        return loanStart;
    }

    public String getId() {
        return id;
    }

    public boolean getActive() {
        return active;
    }

    public boolean getEmailed(){
        return emailed;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public void setEmailed(boolean emailed){
        this.emailed = emailed;
    }
}
