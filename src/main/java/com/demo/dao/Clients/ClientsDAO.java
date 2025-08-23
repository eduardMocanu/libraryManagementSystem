package com.demo.dao.Clients;

import com.demo.model.Client;
import com.demo.model.Loan;

import java.util.HashMap;

public interface ClientsDAO {
    void addClient(Client client);
    String getNameByID(Integer clientId);
    void removeClientById(Integer clientId);
    HashMap<Integer, Loan> getActiveLoansOfClientByClientId(Integer clientId);
    HashMap<Integer, Loan> getHistoryOfClientByClientId(Integer clientId);
}
