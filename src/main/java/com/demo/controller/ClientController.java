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

    public static int removeClient(Integer clientId){
        HashSet<Loan> expiredLoansOfClient = loanSql.getActiveLoansOfClientByClientId(clientId);
        Client client = clientSql.getClientObjById(clientId);
        if(!expiredLoansOfClient.isEmpty()){
            //System.out.println(expiredLoansOfClient.size());
            return 0;//client has expired loans
        } else if (client == null) {
            return 1;//client id is not in db
        }
        if(clientSql.removeClientById(clientId)){
            return 2;//all ok
        }else{
            return 4; //could not remove the client
        }
    }

    public static Client getClientIfExists(Integer clientId){
        return clientSql.getClientObjById(clientId);
    }


}
