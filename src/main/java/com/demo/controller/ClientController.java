package com.demo.controller;

import com.demo.dao.Clients.ClientsDAOMysql;
import com.demo.model.Book;
import com.demo.model.Client;
import com.demo.model.Loan;

import java.util.HashSet;
import java.util.Map;

public abstract class ClientController {

    private static ClientsDAOMysql clientSql = new ClientsDAOMysql();

    public static void addClient(Client client){
        clientSql.addClient(client);
    }

    public static void removeClient(Integer id){
        clientSql.removeClientById(id);
    }


}
