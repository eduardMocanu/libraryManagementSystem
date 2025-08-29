package com.demo.controller;

import com.demo.dao.Clients.ClientsDAO;
import com.demo.dao.Clients.ClientsDAOMysql;
import com.demo.dao.Loans.LoansDAO;
import com.demo.dao.Loans.LoansDAOMysql;
import com.demo.model.Client;
import com.demo.model.Loan;

import java.util.HashMap;
import java.util.HashSet;

public abstract class ClientController {

    private static ClientsDAO clientSql = new ClientsDAOMysql();
    private static LoansDAO loanSql = new LoansDAOMysql();

    public static Integer addClient(Client client){
        return clientSql.addClient(client);
    }

    public static boolean removeClient(Integer clientId){
        HashSet<Loan> expiredLoansOfClient = loanSql.getActiveLoansOfClientByClientId(clientId);
        if(!expiredLoansOfClient.isEmpty()){
            return false;
        }
        clientSql.removeClientById(clientId);
        return true;
    }

    public static Client getClientIfExists(Integer clientId){
        return clientSql.getClientObjById(clientId);
    }


}
