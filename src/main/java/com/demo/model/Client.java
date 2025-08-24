package com.demo.model;

public class Client {
    private final String name, email;
    private final Integer id;

    public Client( Integer id, String name, String email){
        this.name = name;
        this.id = id;
        this.email = email;
    }
    //GETTERS
    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail(){
        return email;
    }
}
