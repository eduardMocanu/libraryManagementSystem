package com.demo.model;

public class Client {
    private final String name, email;

    public Client(String name, String email){
        this.name = name;
        this.email = email;
    }
    //GETTERS
    public String getName() {
        return name;
    }

    public String getEmail(){
        return email;
    }
}
