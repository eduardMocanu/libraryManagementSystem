package com.demo.dao.Clients;

import com.demo.model.Client;
import com.demo.model.Loan;

import java.util.HashMap;

public interface ClientsDAO {
    Integer addClient(Client client);
    String getNameById(Integer clientId);
    boolean removeClientById(Integer clientId);
    Client getClientObjById(Integer clientId);
}
