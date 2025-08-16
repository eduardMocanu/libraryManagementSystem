package com.demo.model;

public class Client {
    private final String name, surname, id, email;

    public Client( String id, String name, String surname, String email){
        this.name = name;
        this.surname = surname;
        this.id = id;
        this.email = email;
    }
    //GETTERS
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getSurname() {
        return surname;
    }

    public String getFullName(){
        return name + " " + surname;
    }

    public String getEmail(){
        return email;
    }
}
