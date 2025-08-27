package com.demo.controller;

import com.demo.dao.Clients.ClientsDAO;
import com.demo.dao.Clients.ClientsDAOMysql;
import com.demo.model.Client;

public abstract class ClientController {

    private static ClientsDAO clientSql = new ClientsDAOMysql();

    public static void addClient(Client client){
        clientSql.addClient(client);
    }

    public static void removeClient(Integer id){
        clientSql.removeClientById(id);
    }


}
