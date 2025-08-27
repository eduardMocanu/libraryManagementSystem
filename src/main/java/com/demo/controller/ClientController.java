package com.demo.controller;

import com.demo.dao.Clients.ClientsDAO;
import com.demo.dao.Clients.ClientsDAOMysql;
import com.demo.model.Client;

public abstract class ClientController {

    private static ClientsDAO clientSql = new ClientsDAOMysql();

    public static Integer addClient(Client client){
        return clientSql.addClient(client);
    }

    public static boolean removeClient(Integer id){
        return clientSql.removeClientById(id);
    }


}
