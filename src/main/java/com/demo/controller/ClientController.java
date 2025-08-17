package com.demo.controller;

import com.demo.model.Book;
import com.demo.model.Client;
import com.demo.model.Loan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class ClientController {

    public static void addClient(Map<String, Client> clients, Client client){
        if(clients.containsKey(client.getId())){
            System.out.println("Can't add the client because the ID already exists in the database");
        }
        else{
            clients.put(client.getId(), client);
            System.out.println("Client " + client.getFullName() + " successfully added with id: " + client.getId());
        }
    }

    public static void removeClient(Map<String, Client> clients, Map<String, Loan> loans, String id){
        if(!clients.containsKey(id)){
            System.out.println("The client ID doesn't exist in the database");
        }
        else{
            boolean found = false;
            for(Loan i: loans.values()){
                if(id.equals(i.getClientId())){
                    found = true;
                    break;
                }
            }
            if(found){
                System.out.println("Can't remove the client because he/she has an active loan");
            }
            else{
                clients.remove(id);
                System.out.println("Client " + id + " successfully removed");
            }
            }

    }

    public static String getNewClientId(Map<String, Client> clients){
        int maximum = 0;
        for(String i: clients.keySet()){
            maximum = Math.max(maximum, Integer.parseInt(i));
        }
        return String.valueOf(maximum+1);
    }

    public static HashSet<Loan> getActiveLoansOfClient(Map<String, Loan> loans, String clientId){
        HashSet<Loan> returnValue = new HashSet<>();
        for (Loan i : loans.values()) {
            if (i.getClientId().equals(clientId) && i.getActive()) {
                returnValue.add(i);
            }
        }
        return returnValue;
    }

    public static void getHistoryOfClient(Map<String, Loan> loans, Map<String, Book> books, String clientId){
        boolean ok = false;
        for(Loan i:loans.values()){
            if(i.getClientId().equals(clientId)){
                ok = true;
                String active = i.getActive() ? "active" : "not active";
                System.out.println("Loan with id " + i.getId() + " is for book " + books.get(i.getBookISBN()).getName() + " and is " + active);
            }
        }
        if(!ok){
            System.out.println("You haven't loaned anything, ever");
        }
    }
}
